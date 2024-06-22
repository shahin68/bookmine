package com.shahin.feature.books.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.shahin.core.database.books.BooksLocalRepository
import com.shahin.core.database.books.model.BookEntity
import com.shahin.core.database.books.sources.BooksDao
import com.shahin.core.network.books.BooksRemoteRepository
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.model.NetworkResponse
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.extensions.toBookEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject


@HiltAndroidTest
@RunWith(MockitoJUnitRunner::class)
class GetBooksUseCaseTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var booksDao: BooksDao

    private lateinit var getBooksUseCase: GetBooksUseCase

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Mock
    lateinit var remoteRepository: BooksRemoteRepository

    @Mock
    lateinit var localRepository: BooksLocalRepository

    @Before
    fun setup() {
        hiltRule.inject()
        getBooksUseCase = GetBooksUseCase(remoteRepository, localRepository)
    }

    @Test
    fun syncBooks_success_shouldInsertBooksIntoLocalRepository() = runTest(testDispatcher) {
        val identifier = "test_identifier"
        val bookItems = listOf(
            BookItem(1, "Title 1", "Description 1", "Author 1", "2023-08-10", "Image 1"),
            BookItem(2, "Title 2", "Description 2", "Author 2", "2023-08-11", "Image 2")
        )
        val response = NetworkResponse.Success(bookItems)

        `when`(remoteRepository.getBooks(identifier)).thenReturn(response)

        // run verifying syncing books
        val result = getBooksUseCase.syncBooks(identifier)
        assertTrue(result is NetworkResponse.Success) // check if result is actually a [NetworkResponse.Success] type
        assertEquals(result, response)
        verify(localRepository).insertBooks(bookItems.map { it.toBookEntity() }) // verify that books are inserting into local db
    }

    /**
     * Scenario where response runs into client error contains missing id field
     */
    @Test
    fun syncBooks_skipInserting_whereNetworkRunsIntoClientErrorException() = runTest(testDispatcher) {
        val identifier = "test_identifier"
        val exceptionMessage = "Failed to parse remote response"
        val exception = Exception(exceptionMessage)
        val expectedResponse = NetworkResponse.ClientError<List<BookItem?>>(exception)

        `when`(remoteRepository.getBooks(identifier)).thenAnswer { expectedResponse }

        val result = getBooksUseCase.syncBooks(identifier)

        // run verifying syncing books are failing
        assertEquals(result, expectedResponse) // client error should match the syncBooks result
        verify(localRepository, never()).insertBooks(emptyList()) // verify local books won't be inserted
    }


    @Test
    fun getLocalBooks_shouldReturnBooksFromLocalRepository() = runTest(testDispatcher) {
        val bookEntities = listOf(
            BookEntity(1, "Book Title 1", "Description 1", "Author 1", "2023-08-10", "Image 1"),
            BookEntity(2, "Book Title 2", "Description 2", "Author 2", "2023-08-11", "Image 2")
        )
        val pagingData = PagingData.from(bookEntities)

        `when`(localRepository.getBooksByTitle("title")).thenReturn(flowOf(pagingData))
        `when`(localRepository.getBooksByTitle("shahin")).thenReturn(flowOf(PagingData.empty())) // mock empty results for "shahin"
        `when`(localRepository.getBooksByTitle("")).thenReturn(flowOf(pagingData)) // mock all for empty query
        `when`(localRepository.getBooksByTitle("2")).thenReturn(flowOf(PagingData.from(listOf(bookEntities[1])))) // mock query the character 2


        // run "title" query
        val localBooks = getBooksUseCase.getLocalBooks("title")
        val localBooksSnapshot: List<Book> = localBooks.asSnapshot()
        assertEquals(localBooksSnapshot.size, 2) // there are 2 items with "title" in the title field
        assertEquals(localBooksSnapshot.first().bookId, 1)
        assertNotNull(localBooksSnapshot.first().title) // check if we even have a title
        assertTrue(localBooksSnapshot.first().title?.equals("Book Title 1", ignoreCase = false) == true) // check if we have the actual title


        // run "shahin" query
        val localBooks1 = getBooksUseCase.getLocalBooks("shahin")
        val localBooksSnapshot1: List<Book> = localBooks1.asSnapshot()
        assertTrue(localBooksSnapshot1.isEmpty()) // list should be empty

        // run "" query
        val localBooks2 = getBooksUseCase.getLocalBooks("")
        val localBooksSnapshot2: List<Book> = localBooks2.asSnapshot()
        assertEquals(localBooksSnapshot2.size, 2)

        // run "2" query
        val localBooks3 = getBooksUseCase.getLocalBooks("2")
        val localBooksSnapshot3: List<Book> = localBooks3.asSnapshot()
        assertEquals(localBooksSnapshot3.size, 1) // one item should be returned
        assertEquals(localBooksSnapshot3.first().bookId, 2) // check if the item is actually the second item
        assertTrue(localBooksSnapshot3.first().title?.contains("2") == true) // check if the item is actually the second item
    }

}
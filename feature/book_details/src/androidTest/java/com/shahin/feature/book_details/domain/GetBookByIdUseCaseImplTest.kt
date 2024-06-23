package com.shahin.feature.book_details.domain

import com.shahin.core.database.books.BooksLocalRepository
import com.shahin.core.database.books.model.BookEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@HiltAndroidTest
@RunWith(MockitoJUnitRunner::class)
class GetBookByIdUseCaseImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var getBookByIdUseCase: GetBookByIdUseCaseImpl

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Mock
    lateinit var localRepository: BooksLocalRepository

    @Before
    fun setup() {
        hiltRule.inject()
        getBookByIdUseCase = GetBookByIdUseCaseImpl(localRepository)
    }


    @Test
    fun getLocalBooks_shouldReturnBooksFromLocalRepository() = runTest(testDispatcher) {
        val bookEntity = BookEntity(1, "Book Title", "Description", "Author", "2023-08-10", "Image")

        `when`(localRepository.getBookById(1)).thenReturn(flowOf(bookEntity)) // mock the exact book
        `when`(localRepository.getBookById(0)).thenReturn(flowOf()) // mock looking for a book that doesn't exist with a wrong id

        // run "1" query
        getBookByIdUseCase.getBookById(1).collectLatest { book ->
            assertNotNull(book) // check if we even get a book in result
            assertEquals(book.bookId, 1) // check if the book id is actually 1
        }


        // run "0" query
        getBookByIdUseCase.getBookById(0).collectLatest { book ->
            assertNotNull(book) // so our book should be null here, cause we gave a wrong id that doesn't exist
        }
    }

}
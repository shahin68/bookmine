package com.shahin.feature.books.presentation.books

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.domain.GetBooksUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BooksViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: BooksViewModel

    @Mock
    lateinit var getBooksUseCase: GetBooksUseCase

    @Before
    fun setUp() {
        viewModel = BooksViewModel(getBooksUseCase)
    }

    @Test
    fun onSearchQueryChangedUpdatesSearchQueryState() = runTest(testDispatcher) {
        val newQuery = "Shahin"
        viewModel.onSearchQueryChanged(newQuery)
        assertEquals(newQuery, viewModel.searchQuery.value)
    }

    @Test
    fun clearSearchQueryResetsSearchQueryState() = runTest(testDispatcher) {
        viewModel.onSearchQueryChanged("Some previously typed query")
        viewModel.clearSearchQuery()
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun getBooksFetchesAndUpdatesBooksState() = runTest(testDispatcher) {
        val query = ""
        val dummyBooksList = List(10) { index ->
            Book(
                bookId = index.toLong(),
                title = "title $index",
                description = "Description $index",
                author = "author",
                releaseDate = "releaseDate",
                image = "https://avatars.githubusercontent.com/u/18089142?v=4"
            )
        }

        val mockedPagingData = PagingData.from(dummyBooksList)
        `when`(getBooksUseCase.getLocalBooks(titleQuery = query)).thenReturn(flowOf(mockedPagingData))

        // call the viewmodel method
        viewModel.getBooks(query)

        testScope.launch {
            // check if the states update properly and we get the db books expected
            val collectedBooks = viewModel.books.asSnapshot()
            assertEquals(collectedBooks.size, 10)// size should be 10 same as our dummy [dummyBooksList]
            assertEquals(collectedBooks[4].bookId, dummyBooksList[4].bookId) // check the 4th item are the same
        }
    }

    @Test
    fun getBooksFilteredByTitle() = runTest(testDispatcher) {
        val query = "5"
        val dummyBooksList = List(10) { index ->
            Book(
                bookId = index.toLong(),
                title = "title $index",
                description = "Description $index",
                author = "author",
                releaseDate = "releaseDate",
                image = "https://avatars.githubusercontent.com/u/18089142?v=4"
            )
        }

        val mockedPagingData = PagingData.from(dummyBooksList)
        `when`(getBooksUseCase.getLocalBooks(titleQuery = query)).thenReturn(flowOf(mockedPagingData))

        // call the viewmodel method
        viewModel.getBooks(query)

        testScope.launch {
            // check if the states update properly and we get the db books expected
            val collectedBooks = viewModel.books.asSnapshot()
            // since we specifically queried "5" we only expect 1 item to match our dummy list titles
            assertEquals(collectedBooks.size, 1)
            // and the title should be the same
            assertEquals(collectedBooks[0].bookId, dummyBooksList[5].bookId)

            // let's also check the actual title
            assertEquals(collectedBooks[0].title, "title 5")
        }
    }
}
package com.shahin.feature.books.presentation.books

import com.shahin.feature.books.domain.GetBooksUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class BooksViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

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

}
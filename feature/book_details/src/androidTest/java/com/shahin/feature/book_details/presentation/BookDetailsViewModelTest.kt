package com.shahin.feature.book_details.presentation

import com.shahin.feature.book_details.data.model.BookDetails
import com.shahin.feature.book_details.domain.GetBookByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class BookDetailsViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: BookDetailsViewModel

    @Mock
    lateinit var getBookByIdUseCase: GetBookByIdUseCase

    @Before
    fun setUp() {
        viewModel = BookDetailsViewModel(getBookByIdUseCase)
    }

    @Test
    fun testGetBookById_updatesBookState() = runTest(testDispatcher) {
        val testBookId = 1.toLong()
        val testBookDetails = BookDetails(
            bookId = testBookId,
            // other field don't matter for this test
        )

        // Mock the use case to return a flow with the test book details
        `when`(getBookByIdUseCase.getBookById(testBookId)).thenReturn(flowOf(testBookDetails))

        viewModel.getBookById(testBookId)

        testScope.launch {
            // check if the [viewModel.book] state updates correctly
            viewModel.book.collectLatest { book ->
                // check if we got a book back from local db
                assertNotNull(book)

                // and if if we even got the correct book by verifying the id
                assertEquals(book?.bookId, testBookDetails.bookId)
            }
        }
    }

    @Test
    fun testGetBookById_wrongBookId() = runTest(testDispatcher) {
        val testBookId = 3.toLong()
        val testBookDetailsWeExpected = BookDetails(
            bookId = 1.toLong(),
            // other field don't matter for this test
        )

        // Mock the use case to return a flow with the test book details
        `when`(getBookByIdUseCase.getBookById(testBookId)).thenReturn(flowOf()) // flow of nothing

        viewModel.getBookById(testBookId)

        Thread.sleep(1000)

        testScope.launch {
            // check if the [viewModel.book] state updates correctly regardless
            viewModel.book.collectLatest { book ->
                // book should still be null and that's it. nothing else!
                assertNull(book)
            }
        }
    }

}
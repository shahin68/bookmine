package com.shahin.core.network.books

import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.books.services.BooksMockyIoApi
import com.shahin.core.network.model.NetworkResponse
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import retrofit2.Response

class BooksRemoteRepositoryImplTest {

    private lateinit var booksRemoteRepository: BooksRemoteRepository
    private lateinit var mockBooksApi: BooksMockyIoApi
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setUp() {
        mockBooksApi = mock()
        booksRemoteRepository = BooksRemoteRepositoryImpl(
            mockBooksApi,
            testDispatcher
        )
    }

    @Test
    fun testGetBooksSuccessResponse() = runTest(testScheduler) {
        val mockResponse = Response.success(
            listOf<BookItem?>(
                BookItem(id = 1, title = "Book 1"),
                BookItem(id = 2, title = "Book 2")
            )
        )

        `when`(mockBooksApi.getBooks(any())).thenReturn(mockResponse)

        val result = booksRemoteRepository.getBooks("testId")

        assertTrue(result is NetworkResponse.Success)
        when (result) {
            is NetworkResponse.Success -> {
                val books = result.data
                assertEquals(2, books?.size)
                assertEquals(1, books?.get(0)?.id)
                assertEquals("Book 1", books?.get(0)?.title)
            }
            else -> {}
        }
    }

    @Test
    fun testGetBooksErrorResponse() = runTest(testScheduler) {
        val mockResponse = Response.error<List<BookItem?>>(
            500,
            "".toResponseBody()
        )

        `when`(mockBooksApi.getBooks(any())).thenReturn(mockResponse)

        val result = booksRemoteRepository.getBooks("testId")

        assertTrue(result is NetworkResponse.ServerError)
        when (result) {
            is NetworkResponse.ServerError -> {
                val error = result.error
                assertEquals(500, error.errorCode)
            }
            else -> {}
        }
    }

}
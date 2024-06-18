package com.shahin.core.network

import com.shahin.core.network.model.ErrorReason
import com.shahin.core.network.model.NetworkResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response
import java.io.IOException

class NetworkResponseWrapperTest {

    private val networkResponseWrapper = NetworkResponseWrapper()

    @Test
    fun `networkResponseOf returns Success for successful response`() {
        val mockResponse = Response.success("Success Data")
        val result = networkResponseWrapper.networkResponseOf { mockResponse }
        assertEquals(NetworkResponse.Success("Success Data"), result)
    }

    /**
     * It really doesn't matter what's inside our response body
     * [networkResponseOf] should work the same as we are passing whatever as server response
     */
    data class MockResponseBody(
        val id: Int,
        val bookDetails: String
    )

    /**
     * Asserting successful response handling functionality
     */
    @Test
    fun `mockito networkResponseOf returns Success for successful response`() {
        val mockResponse = Mockito.mock(Response::class.java)
        Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
        Mockito.`when`(mockResponse.code()).thenReturn(200)

        val responseBody = mutableListOf<MockResponseBody>()
        repeat(10) {
            responseBody.add(
                MockResponseBody(
                    id = it,
                    bookDetails = "details $it"
                )
            )
        }

        Mockito.`when`(mockResponse.body()).thenReturn(responseBody)

        val result = networkResponseWrapper.networkResponseOf { mockResponse }

        assertEquals(NetworkResponse.Success<List<MockResponseBody>>(responseBody), result)
    }

    @Test
    fun `networkResponseOf returns ServerError for unsuccessful response`() {
        val mockResponse = Mockito.mock(Response::class.java)
        Mockito.`when`(mockResponse.isSuccessful).thenReturn(false)
        Mockito.`when`(mockResponse.code()).thenReturn(500)
        Mockito.`when`(mockResponse.message()).thenReturn("Server error")

        val result = networkResponseWrapper.networkResponseOf { mockResponse }

        assertEquals(NetworkResponse.ServerError<Any>(ErrorReason(500, "Server error")), result)
    }

    @Test
    fun `networkResponseOf returns NetworkError for IOException`() {
        val exception = IOException("IO Exception")
        val result = networkResponseWrapper.networkResponseOf<Any> { throw exception }
        assertEquals(NetworkResponse.NetworkError, result)
    }

    @Test
    fun `networkResponseOf returns ClientError for other exceptions`() {
        val clientSideException = Exception("Generic Exception")
        val result = networkResponseWrapper.networkResponseOf<Any> { throw clientSideException }
        assertEquals(NetworkResponse.ClientError<Any>(clientSideException), result)
    }

}
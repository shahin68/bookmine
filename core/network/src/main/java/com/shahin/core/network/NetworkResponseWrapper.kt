package com.shahin.core.network

import com.shahin.core.network.model.ErrorReason
import com.shahin.core.network.model.NetworkResponse
import retrofit2.Response
import java.io.IOException

/**
 * A wrapper handler to standardize handling of network responses.
 *
 *
 * Returns:
 * [NetworkResponse.Success] wrapping the response body when invoked [Response] (retrofitResponse)
 * is successful
 *
 * Returns:
 * [NetworkResponse.ServerError] wrapping [ErrorReason] containing error code and response message when
 * invoked [Response] (retrofitResponse) responds with remote side failure
 *
 * Returns:
 *  * [NetworkResponse.NetworkError] containing no data when invoked [Response] (retrofitResponse) is
 *  * failed due to client side failure of no network connection or unknown host
 *
 * Returns:
 * [NetworkResponse.ClientError] containing [Throwable] when invoked [Response] (retrofitResponse) is
 * failed due to client side failure of any reason other than [NetworkResponse.NetworkError]
 * Failures such as parsing and serialization exceptions, etc..
 */
open class NetworkResponseWrapper {

    inline fun <T: Any> networkResponseOf(retrofitResponse: () -> Response<T>) : NetworkResponse<T> {
        try {
            val response = retrofitResponse()
            return if (response.isSuccessful) {
                NetworkResponse.Success(response.body())
            } else {
                NetworkResponse.ServerError(
                    ErrorReason(
                        errorCode = response.code(),
                        errorMessage = response.message()
                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return NetworkResponse.NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            return NetworkResponse.ClientError(e)
        }
    }

}
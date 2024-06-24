package com.shahin.core.network.model

/**
 * Remote response wrapper
 *
 * [Success] represents a successful network response wrapping data of type [T]
 * [ServerError] represents an remote server error response with a specified error reason in [ErrorReason]
 * [ClientError] represents any failure except network connection occurring from the client side, such as parsing failure, wrapping a [Throwable]
 * [NetworkResponse] represents a client side network error such no internet connection or unknown host failures with no specific type information [Nothing]
 * */
sealed class NetworkResponse<out T> {

    data class Success<T>(val data: T? = null) : NetworkResponse<T>()

    data class ServerError<T>(val error: ErrorReason) : NetworkResponse<T>()

    data class ClientError<T>(val throwable: Throwable) : NetworkResponse<T>()

    data object NetworkError : NetworkResponse<Nothing>()

}
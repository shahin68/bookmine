package com.shahin.core.network.model

/**
 * representing network error response
 *
 * This is used because our http endpoint doesn't have a representable error body in case of failure
 */
data class ErrorReason(
    val errorCode: Int,
    val errorMessage: String
)
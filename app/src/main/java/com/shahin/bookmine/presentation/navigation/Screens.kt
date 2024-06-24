package com.shahin.bookmine.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object ScreenBooks

@Serializable
data class ScreenBookDetails(
    val bookId: Long
)
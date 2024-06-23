package com.shahin.feature.books.data.model

import androidx.annotation.DrawableRes

/**
 * Represents a Book
 *
 * [bookId] should never be null
 */
data class Book(
    val bookId: Long,
    val title: String? = null,
    val description: String? = null,
    val author: String? = null,
    val releaseDate: String? = null,
    val image: String? = null,
    @DrawableRes val placeHolder: Int? = null,
    @DrawableRes val errorImage: Int? = null
)
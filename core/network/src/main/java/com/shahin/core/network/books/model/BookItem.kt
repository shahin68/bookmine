package com.shahin.core.network.books.model

import com.google.gson.annotations.SerializedName


/**
 * Model representing a book item received from our remote source
 *
 * [BookItem] as remote response isn't a reliable source of truth since there are typos in some fields,
 *
 * Only to be used to parse the response and not to be used directly while creating UI or Database.
 * map [BookItem] to your appropriate models.
 */
data class BookItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("author")
    val author: String? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("image")
    val image: String? = null,
)


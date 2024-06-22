package com.shahin.feature.books.extensions

import com.shahin.core.database.books.model.BookEntity
import com.shahin.core.network.books.model.BookItem

fun BookItem.toBookEntity(): BookEntity {
    return BookEntity(
        id = id.toLong(),
        title = title,
        description = description,
        author = author,
        releaseDate = releaseDate,
        image = image
    )
}
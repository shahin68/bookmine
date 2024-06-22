package com.shahin.feature.books.extensions

import com.shahin.core.database.books.model.BookEntity
import com.shahin.feature.books.data.model.Book

fun BookEntity.toBook(): Book {
    return Book(
        bookId = id,
        title = title,
        description = description,
        author = author,
        releaseDate = releaseDate,
        image = image
    )
}
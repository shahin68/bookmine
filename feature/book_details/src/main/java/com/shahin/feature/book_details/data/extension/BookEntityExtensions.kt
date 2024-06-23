package com.shahin.feature.book_details.data.extension

import com.shahin.core.database.books.model.BookEntity
import com.shahin.feature.book_details.data.model.BookDetails

fun BookEntity.toBookDetails(): BookDetails {
    return BookDetails(
        bookId = id,
        title = title,
        description = description,
        author = author,
        releaseDate = releaseDate,
        image = image
    )
}
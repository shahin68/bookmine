package com.shahin.feature.book_details.domain

import com.shahin.feature.book_details.data.model.BookDetails
import kotlinx.coroutines.flow.Flow

interface GetBookByIdUseCase {
    fun getBookById(bookId: Long): Flow<BookDetails>
}
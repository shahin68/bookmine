package com.shahin.feature.book_details.domain

import com.shahin.core.database.books.BooksLocalRepository
import com.shahin.feature.book_details.data.extension.toBookDetails
import com.shahin.feature.book_details.data.model.BookDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetBookByIdUseCaseImpl @Inject constructor(
    private val localRepository: BooksLocalRepository
): GetBookByIdUseCase {

    override fun getBookById(bookId: Long): Flow<BookDetails> {
        return localRepository.getBookById(id = bookId).map {
            it.toBookDetails()
        }
    }

}
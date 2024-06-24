package com.shahin.core.database.books

import androidx.paging.PagingData
import com.shahin.core.database.books.model.BookEntity
import kotlinx.coroutines.flow.Flow

interface BooksLocalRepository {
    suspend fun insertBook(book: BookEntity)
    suspend fun insertBooks(books: List<BookEntity>)
    fun getBooksByTitle(titleQuery: String): Flow<PagingData<BookEntity>>
    fun getBookById(id: Long): Flow<BookEntity>
}
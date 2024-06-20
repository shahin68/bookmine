package com.shahin.core.network.books

import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.model.NetworkResponse

interface BooksRemoteRepository {
    suspend fun getBooks(identifier: String): NetworkResponse<List<BookItem?>>
}
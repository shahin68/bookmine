package com.shahin.feature.books.domain

import androidx.paging.PagingData
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.model.NetworkResponse
import com.shahin.feature.books.data.model.Book
import kotlinx.coroutines.flow.Flow

interface GetBooksUseCase {
    suspend fun syncBooks(identifier: String): NetworkResponse<List<BookItem?>>
    fun getLocalBooks(titleQuery: String): Flow<PagingData<Book>>
}
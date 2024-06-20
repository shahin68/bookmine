package com.shahin.core.network.books

import com.shahin.core.common.di.IoDispatcher
import com.shahin.core.network.NetworkResponseWrapper
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.books.services.BooksMockyIoApi
import com.shahin.core.network.model.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BooksRemoteRepositoryImpl @Inject constructor(
    private val booksMockyIoApi: BooksMockyIoApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): NetworkResponseWrapper(), BooksRemoteRepository {

    override suspend fun getBooks(identifier: String): NetworkResponse<List<BookItem?>> {
        return withContext(dispatcher) {
            networkResponseOf {
                booksMockyIoApi.getBooks(identifier)
            }
        }
    }

}
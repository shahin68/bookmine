package com.shahin.feature.books.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.shahin.core.database.books.BooksLocalRepository
import com.shahin.core.network.books.BooksRemoteRepository
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.model.NetworkResponse
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.data.extensions.toBook
import com.shahin.feature.books.data.extensions.toBookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val remoteRepository: BooksRemoteRepository,
    private val localRepository: BooksLocalRepository
) {

    /**
     * Usage syncs the remote with local db
     *
     * fetches the latest remote list of [BookItem]s
     * and stores as a list of [BookEntity] in our local db
     */
    suspend fun syncBooks(identifier: String): NetworkResponse<List<BookItem?>> {
        val response = remoteRepository.getBooks(identifier = identifier)
        if (response is NetworkResponse.Success) {
            localRepository.insertBooks(
                books = response.data?.filterNotNull()?.map {
                    it.toBookEntity()
                } ?: emptyList()
            )
        }
        return response
    }

    /**
     * We don't want to represent output as a list of [BookEntity]s
     *
     * [Book] is a model created to represent UI for a book item
     * Therefore [BookEntity] will be mapped to [Book]
     *
     * This is because we want have separate control over our db [BookEntity] item without affecting
     * the [Book] UI model
     */
    fun getLocalBooks(titleQuery: String): Flow<PagingData<Book>> {
        return localRepository.getBooksByTitle(titleQuery = titleQuery.ifBlank { "" }).map { pagingData ->
            pagingData.map { bookEntity ->
                bookEntity.toBook()
            }
        }
    }

}
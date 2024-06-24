package com.shahin.core.database.books

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shahin.core.common.di.IoDispatcher
import com.shahin.core.database.books.model.BookEntity
import com.shahin.core.database.books.sources.BooksDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BooksLocalRepositoryImpl @Inject constructor(
    private val booksDao: BooksDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BooksLocalRepository {
    override suspend fun insertBook(book: BookEntity) {
        withContext(ioDispatcher) {
            booksDao.insertBook(book)
        }
    }

    override suspend fun insertBooks(books: List<BookEntity>) {
        withContext(ioDispatcher) {
            booksDao.insertBooks(books)
        }
    }

    override fun getBooksByTitle(titleQuery: String): Flow<PagingData<BookEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 100
            )
        ) {
            booksDao.getBooksByTitle(titleQuery)
        }.flow
    }

    override fun getBookById(id: Long): Flow<BookEntity> {
        return booksDao.getBookById(id)
    }

}
package com.shahin.core.database.books.sources

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shahin.core.database.books.model.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BooksDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(bookEntity: BookEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Transaction
    @Delete
    suspend fun deleteBooks(bookEntity: BookEntity)

    /**
     * [getBooksByTitle] provides a [title] argument that can be used to filter [BookEntity] list
     * [getBooksByTitle] returns all [BookEntity]s when [title] is empty and not blank
     */
    @Transaction
    @Query(" SELECT * FROM books WHERE title LIKE '%' || :title || '%' ")
    fun getBooksByTitle(title: String): PagingSource<Int, BookEntity>

    /**
     * [getBookById] provides a [id] argument that can be used to filter one [BookEntity] matching the input
     */
    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<BookEntity>

}
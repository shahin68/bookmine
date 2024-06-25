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
    @Query("""
    SELECT * FROM books 
    WHERE title LIKE '%' || :title || '%' 
    ORDER BY
        CASE 
            WHEN release_date LIKE '% BC' THEN -CAST(SUBSTR(release_date, 1, LENGTH(release_date) - 3) AS INT)
            ELSE CAST(SUBSTR(release_date, -4) AS INT)
        END DESC,
        CASE 
            WHEN release_date LIKE '%/%/%' THEN STRFTIME('%Y-%m-%d', release_date)
            ELSE STRFTIME('%Y-01-01', release_date || '-01-01')
        END DESC
    """)
    fun getBooksByTitle(title: String): PagingSource<Int, BookEntity>

    /**
     * [getBookById] provides a [id] argument that can be used to filter one [BookEntity] matching the input
     */
    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<BookEntity>

}
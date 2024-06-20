package com.shahin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shahin.core.database.books.model.BookEntity
import com.shahin.core.database.books.sources.BooksDao

/**
 * Creates database
 *
 * No type converters needed as we only have one simple [BookEntity] item
 */
@Database(
    entities = [
        BookEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun booksDao(): BooksDao

}
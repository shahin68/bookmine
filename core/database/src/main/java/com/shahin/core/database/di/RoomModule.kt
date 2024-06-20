package com.shahin.core.database.di

import android.content.Context
import androidx.room.Room
import com.shahin.core.database.AppDatabase
import com.shahin.core.database.BuildConfig.DATABASE_NAME
import com.shahin.core.database.books.sources.BooksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    fun provideBooksDao(appDatabase: AppDatabase): BooksDao {
        return appDatabase.booksDao()
    }

}
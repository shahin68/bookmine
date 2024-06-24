package com.shahin.core.database.di

import com.shahin.core.database.books.BooksLocalRepository
import com.shahin.core.database.books.BooksLocalRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BooksLocalRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBooksLocalRepository(booksLocalRepositoryImpl: BooksLocalRepositoryImpl): BooksLocalRepository

}
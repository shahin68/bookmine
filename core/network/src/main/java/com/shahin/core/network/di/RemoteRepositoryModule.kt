package com.shahin.core.network.di

import com.shahin.core.network.books.BooksRemoteRepository
import com.shahin.core.network.books.BooksRemoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBooksRemoteRepository(booksRemoteRepositoryImpl: BooksRemoteRepositoryImpl): BooksRemoteRepository


}
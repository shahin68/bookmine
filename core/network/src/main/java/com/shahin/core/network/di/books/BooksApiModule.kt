package com.shahin.core.network.di.books

import com.shahin.core.network.books.services.BooksMockyIoApi
import com.shahin.core.network.common.Constants.BOOKS_RETROFIT_CLIENT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BooksApiModule {

    @Provides
    @Singleton
    fun provideBooksApi(
        @Named(BOOKS_RETROFIT_CLIENT) retrofit: Retrofit
    ): BooksMockyIoApi {
        return retrofit.create(BooksMockyIoApi::class.java)
    }

}
package com.shahin.core.network.di.books

import com.google.gson.GsonBuilder
import com.shahin.core.network.BuildConfig
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.books.model.serialization.BookItemDeserializer
import com.shahin.core.network.common.Constants.BOOKS_RETROFIT_CLIENT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BooksNetworkModule {

    @Provides
    @Singleton
    fun provideGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapter(BookItem::class.java, BookItemDeserializer())
                .create()
        )
    }

    @Provides
    @Singleton
    @Named(value = BOOKS_RETROFIT_CLIENT)
    fun provideBooksRetrofitClient(
        okHttpClient: OkHttpClient,
        gsonConverter: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL_MOCKY_IO)
            .addConverterFactory(gsonConverter)
            .build()
    }
}
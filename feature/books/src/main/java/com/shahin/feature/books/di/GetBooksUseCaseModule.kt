package com.shahin.feature.books.di

import com.shahin.feature.books.domain.GetBooksUseCase
import com.shahin.feature.books.domain.GetBooksUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GetBooksUseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetBooksUseCase(getBooksUseCaseImpl: GetBooksUseCaseImpl): GetBooksUseCase


}
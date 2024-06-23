package com.shahin.feature.book_details.di

import com.shahin.feature.book_details.domain.GetBookByIdUseCase
import com.shahin.feature.book_details.domain.GetBookByIdUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GetBookByIdUseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetBookByIdUseCase(getBookByIdUseCaseImpl: GetBookByIdUseCaseImpl): GetBookByIdUseCase


}
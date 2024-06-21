package com.shahin.core.database.di

import com.shahin.core.database.BuildConfig
import com.shahin.core.database.common.Constants.DB_PASSPHRASE_KEY
import com.shahin.core.database.extensions.hash
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object BuildModule {

    @Provides
    @Named(DB_PASSPHRASE_KEY)
    fun getCurrentDbPassPhrase(): String = BuildConfig.DB_PASSPHRASE.hash()

}
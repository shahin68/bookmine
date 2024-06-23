package com.shahin.core.database.di

import com.shahin.core.common.extensions.hash
import com.shahin.core.database.BuildConfig
import com.shahin.core.database.common.Constants.DB_PASSPHRASE_KEY
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
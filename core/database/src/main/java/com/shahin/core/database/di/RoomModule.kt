package com.shahin.core.database.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.shahin.core.database.AppDatabase
import com.shahin.core.database.BuildConfig.DATABASE_NAME
import com.shahin.core.database.books.sources.BooksDao
import com.shahin.core.database.common.Constants.DB_PASSPHRASE_KEY
import com.shahin.core.database.common.Constants.DB_PREFS
import com.shahin.core.database.common.Constants.SQL_CIPHER
import com.shahin.core.database.encryption.SupportFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.nio.charset.StandardCharsets
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        supportFactory: SupportFactory
    ): AppDatabase {
        val room = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .openHelperFactory(supportFactory.supportFactory)
            .build()

        if (supportFactory.passphraseChanged == true) {
            room.query("PRAGMA rekey = '${supportFactory.currentPassphrase}';", emptyArray())
        }

        return room
    }

    @Provides
    fun provideBooksDao(appDatabase: AppDatabase): BooksDao {
        return appDatabase.booksDao()
    }

    @Provides
    @Singleton
    fun provideSupportFactory(
        @ApplicationContext context: Context,
        @Named(DB_PASSPHRASE_KEY) currentPassphrase: String,
    ): SupportFactory {
        return try {
            System.loadLibrary(SQL_CIPHER)

            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            val sharedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
                DB_PREFS,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val oldPassphrase = sharedPrefs.getString(DB_PASSPHRASE_KEY, null)

            val passwordChanged = oldPassphrase?.equals(currentPassphrase, ignoreCase = false)?.not() ?: false

            sharedPrefs.edit().putString(DB_PASSPHRASE_KEY, currentPassphrase).apply()

            SupportFactory(
                supportFactory = SupportOpenHelperFactory(
                    oldPassphrase?.toByteArray(StandardCharsets.UTF_8)
                        ?: currentPassphrase.toByteArray(StandardCharsets.UTF_8)
                ),
                passphraseChanged = passwordChanged,
                currentPassphrase = currentPassphrase
            )
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
            SupportFactory()
        }
    }


}
package com.shahin.core.network.di

import com.shahin.core.network.books.services.BooksMockyIoApi
import com.shahin.core.network.test.BuildConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named


@HiltAndroidTest
class BooksNetworkModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var booksApi: BooksMockyIoApi

    @Inject
    lateinit var gsonConverterFactory: GsonConverterFactory

    @Inject
    @Named("books-retrofit-client")
    lateinit var retrofit: Retrofit

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testProvidesBooksApi() {
        assertNotNull(booksApi)
    }

    @Test
    fun testProvideGsonConverter() {
        assertNotNull(gsonConverterFactory)
    }

    @Test
    fun testProvideBooksRetrofitClient() {
        assertNotNull(retrofit)
        assertNotNull(retrofit.baseUrl())
        assertEquals(retrofit.baseUrl().toString(), BuildConfig.BASE_URL_MOCKY_IO)
        assertNotNull(retrofit.converterFactories().find { it is GsonConverterFactory })
    }
}
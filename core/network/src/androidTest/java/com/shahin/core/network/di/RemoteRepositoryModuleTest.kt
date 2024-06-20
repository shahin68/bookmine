package com.shahin.core.network.di

import com.shahin.core.network.books.BooksRemoteRepository
import com.shahin.core.network.books.BooksRemoteRepositoryImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RemoteRepositoryModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var booksRemoteRepository: BooksRemoteRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testBindBooksRemoteRepository() {
        assertTrue(booksRemoteRepository is BooksRemoteRepositoryImpl)
    }
}
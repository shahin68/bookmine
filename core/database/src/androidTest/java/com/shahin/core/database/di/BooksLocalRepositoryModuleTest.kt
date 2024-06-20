package com.shahin.core.database.di

import com.shahin.core.database.books.BooksLocalRepository
import com.shahin.core.database.books.BooksLocalRepositoryImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class BooksLocalRepositoryModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var booksLocalRepository: BooksLocalRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testBindBooksLocalRepository() {
        assertTrue(booksLocalRepository is BooksLocalRepositoryImpl)
    }
}
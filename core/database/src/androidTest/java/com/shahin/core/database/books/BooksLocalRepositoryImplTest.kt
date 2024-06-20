package com.shahin.core.database.books

import com.shahin.core.database.books.model.BookEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
class BooksLocalRepositoryImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Inject
    lateinit var booksLocalRepository: BooksLocalRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun insertBookAndRetrieveById() = runTest(testDispatcher) {
        val book = BookEntity(
            id = 1,
            title = "Book",
            author = "Author",
            description = "Description",
            releaseDate = "2024",
            image = "image_url"
        )
        booksLocalRepository.insertBook(book)

        val retrievedBook = booksLocalRepository.getBookById(1).first()
        assertEquals(book, retrievedBook)
        assertEquals(book.title, retrievedBook.title)
        assertEquals("Book", retrievedBook.title)
    }

    @Test
    fun testInsertBooks() = runTest {
        val books = listOf(
            BookEntity(
                id = 1,
                title = "Book 1",
                author = "Author 1",
                description = "Description 1",
                releaseDate = "2014"
            ),
            BookEntity(
                id = 2,
                title = "Book 2",
                author = "Author 2",
                description = "Description 2",
                releaseDate = "202 BC"
            )
        )


        booksLocalRepository.insertBooks(books)

        val secondBook = booksLocalRepository.getBookById(2).first()

        assertEquals(secondBook.id, 2)
        assertEquals(secondBook.title, "Book 2")
    }

    @Test
    fun testGetBookById() = runTest(testDispatcher) {
        val book = BookEntity(
            id = 1,
            title = "Book",
            author = "Author",
            description = "Description",
            releaseDate = "2023-01-01"
        )
        booksLocalRepository.insertBook(book)

        val fetchedBook = booksLocalRepository.getBookById(1).first()
        assertEquals(book, fetchedBook)
    }
}
package com.shahin.core.database.books.sources

import androidx.paging.PagingSource
import com.shahin.core.database.AppDatabase
import com.shahin.core.database.books.model.BookEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class BooksDaoTest {

    private val dummyBooks = listOf(
        BookEntity(
            id = 1,
            title = "Book 1",
            description = "Author A",
            author = "Desc 1",
            releaseDate = "2023-01-01",
            image = "image1"
        ),
        BookEntity(
            id = 2,
            title = "Book 2",
            description = "Author B",
            author = "Desc 2",
            releaseDate = "2023-02-02",
            image = "image2"
        ),
        BookEntity(
            id = 3,
            title = "Another Book", // changed the string formatting to validate filtering by title no matter where the characters match
            description = "Author C",
            author = "Desc 3",
            releaseDate = "2023-03-03",
            image = "image3"
        )
    )

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    private lateinit var booksDao: BooksDao

    @Before
    fun setUp() {
        hiltRule.inject()
        booksDao = database.booksDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertBookAndRetrieveById() = runTest {
        val book = BookEntity(
            id = 1,
            title = "Test Book",
            author = "Test Author",
            description = "Test Description",
            releaseDate = "2023-06-20",
            image = "test_image_url"
        )
        booksDao.insertBook(book)

        val retrievedBook = booksDao.getBookById(1).first()
        assertEquals(book, retrievedBook)
    }

    @Test
    fun insertMultipleBooksAndRetrieveByTitleWhenQueryIsValid() = runTest {
        booksDao.insertBooks(dummyBooks)

        // Retrieve books with "Book" in the title
        val pagingSource = booksDao.getBooksByTitle("Book")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // sizes should be the same since we have 3 items with "Book" in their title
        assertEquals(
            dummyBooks.size,
            (result as PagingSource.LoadResult.Page).data.size
        )

        // and the first item should the BookEntity with id = 3
        assertEquals(
            3, // expected id the 3
            (result as PagingSource.LoadResult.Page).data[0].id
        )
        assertEquals(
            dummyBooks[2].id,
            (result as PagingSource.LoadResult.Page).data[0].id
        )
    }

    @Test
    fun insertMultipleBooksAndRetrieveByTitleFindSpecificTitle() = runTest {
        booksDao.insertBooks(dummyBooks)

        // Retrieve books with "another" in the title - the case sensitivity should not matter
        val pagingSource = booksDao.getBooksByTitle("another")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )
        assertEquals(
            listOf(dummyBooks[2]),
            (result as PagingSource.LoadResult.Page).data
        )
        assertTrue(result.data[0].title?.contains("Another", ignoreCase = true) == true)
    }

    @Test
    fun insertMultipleBooksAndRetrieveByTitleWhenQueryIsEmpty() = runTest {
        booksDao.insertBooks(dummyBooks)

        // Retrieve all books (empty title)
        val allBooksPagingSource = booksDao.getBooksByTitle("")
        val allBooksResult = allBooksPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // since a new reordering is added to the dao we'll have a slightly different expected list
        // Reordering is from oldest to newest, so the first item should be [id = 3]
        assertEquals(
            dummyBooks[2].id,
            (allBooksResult as PagingSource.LoadResult.Page).data[0].id
        )

        // now for checking if we're getting all the results we should just check the size
        assertEquals(
            dummyBooks.size,
            (allBooksResult as PagingSource.LoadResult.Page).data.size
        )
    }

    @Test
    fun deleteBookAndVerifyEmptyResult() = runTest {
        val book = BookEntity(1, "Book 1", "Author A", "Desc 1", "2023-01-01", "image1")
        booksDao.insertBook(book)
        booksDao.deleteBooks(book)

        val retrievedBook = booksDao.getBookById(1).first()
        assertEquals(null, retrievedBook)
    }
}
package com.shahin.feature.book_details.presentation

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.shahin.feature.book_details.data.model.BookDetails
import com.shahin.feature.book_details.domain.GetBookByIdUseCase
import com.shahin.feature.book_details.presentation.ui.theme.BooksDetailsPreviewTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var useCase: GetBookByIdUseCase

    private lateinit var viewModel: BookDetailsViewModel

    @Before
    fun setUp() {
        viewModel = BookDetailsViewModel(useCase)
    }

    @Test
    fun testBookDetailsDisplayed() {
        val bookId = 1.toLong()
        val dummyBookDetails = BookDetails(
            bookId = bookId,
            title = "Book title",
            author = "The guy",
            releaseDate = "2023",
            description = "description",
            image = "image_url"
        )
        `when`(useCase.getBookById(bookId)).thenReturn(flowOf(dummyBookDetails))

        composeTestRule.setContent {
            BooksDetailsPreviewTheme {
                BookDetailsScreen(
                    bookId = bookId,
                    bookDetailsViewModel = viewModel,
                    onClose = {}
                )
            }
        }


        composeTestRule.waitForIdle()

        // check the content
        composeTestRule.onNodeWithTag("details-title").assertTextEquals("Book title")
        composeTestRule.onNodeWithTag("details-release-date").assertTextEquals("2023")
        composeTestRule.onNodeWithTag("details-description").assertTextEquals("description")
        composeTestRule.onNodeWithTag("details-author").assertTextContains("Author: The guy")

        // Verify that the ViewModel's getBookById() was called
        verify(useCase).getBookById(bookId)
    }

    @Test
    fun testBackButtonClosesScreen() {
        var onCloseCalled = false

        val bookId = 1.toLong()
        val dummyBookDetails = BookDetails(
            bookId = bookId,
            title = "Book title",
            author = "The guy",
            releaseDate = "2023",
            description = "description",
            image = "image_url"
        )
        `when`(useCase.getBookById(bookId)).thenReturn(flowOf(dummyBookDetails))

        composeTestRule.setContent {
            BooksDetailsPreviewTheme {
                BookDetailsScreen(
                    bookId = 1.toLong(),
                    bookDetailsViewModel = viewModel,
                    onClose = { onCloseCalled = true }
                )
            }
        }

        // Click the back button
        composeTestRule.onNodeWithTag("back-button-container").performClick()

        // Assert that the onClose lambda was called
        assert(onCloseCalled)
    }
}
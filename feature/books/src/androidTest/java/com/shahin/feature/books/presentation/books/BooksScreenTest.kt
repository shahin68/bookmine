package com.shahin.feature.books.presentation.books

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.domain.GetBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BooksScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var getBooksUseCase: GetBooksUseCase

    private lateinit var booksViewModel: BooksViewModel

    @Before
    fun setUp() {
        booksViewModel = BooksViewModel(getBooksUseCase)
    }

    @Test
    fun testSearchTriggersBookLoading() {
        val query = "book"
        val ongoingSyncState = MutableStateFlow(false)
        val testBooks = listOf(
            Book(1, "Book 1", "desc 1", "Author 1", "2023", null),
            Book(2, "Book 2", "desc 2", "Author 2", "2022", null))
        val pagingData = PagingData.from(testBooks)

        // Mock the behavior of getLocalBooks before setting the content
        `when`(getBooksUseCase.getLocalBooks(anyString())).thenReturn(flowOf(pagingData))

        composeTestRule.setContent {
            BooksScreen(
                booksViewModel = booksViewModel,
                ongoingSyncInProgress = ongoingSyncState,
                queryHint = "Search for books",
                onBookItemClick = {},
                emptyPlaceholder = {}
            )
        }

        // Enter a search query, and we have a test field inside the search view with tag "text-field"
        composeTestRule.onNodeWithTag("text-field")
            .performTextInput(query)


        composeTestRule.waitForIdle()

        // Verify that the list of books is displayed with 2 book items
        composeTestRule.onNodeWithTag("books-list")
            .onChildren()
            .assertCountEquals(2)
    }

    @Test
    fun testEmptyPlaceholderIsDisplayed() {
        val ongoingSyncState = MutableStateFlow(false)
        composeTestRule.setContent {
            HomeScreenContent(
                ongoingSyncInProgress = ongoingSyncState,
                booksFlow = flowOf(PagingData.empty()),
                searchQuery = MutableStateFlow(""),
                queryHint = "",
                onQueryChange = {},
                onClearQuery = {},
                onBookItemClick = {},
                emptyPlaceholder = {
                    Text("No books found")
                }
            )
        }

        // a view with the text placeholder text we inserted should be visible
        composeTestRule.onNodeWithText("No books found").assertIsDisplayed()

        // books list should not be visible
        composeTestRule.onNodeWithTag("books-list").assertIsNotDisplayed()
    }

}
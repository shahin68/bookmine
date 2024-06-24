package com.shahin.feature.books.presentation.books.list

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.paging.PagingData
import com.shahin.feature.books.data.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BooksListViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEmptyPlaceholderIsDisplayedWhenBooksAreMissing() {
        val ongoingSyncState = MutableStateFlow(false)
        composeTestRule.setContent {
            BooksListView(
                ongoingSyncInProgress = ongoingSyncState,
                booksFlow = flowOf(PagingData.empty()),
                onBookItemClick = {},
                emptyPlaceholder = {
                    Text("No books found")
                }
            )
        }

        composeTestRule.onNodeWithTag("placeholder").assertIsDisplayed()
            .onChildAt(0).assertTextEquals("No books found")
    }

    @Test
    fun testProgressBarIsDisplayedWhenLoading() {
        val ongoingSyncState = MutableStateFlow(true)
        composeTestRule.setContent {
            BooksListView(
                ongoingSyncInProgress = ongoingSyncState,
                booksFlow = flowOf(PagingData.empty()),
                onBookItemClick = {},
                emptyPlaceholder = {}
            )
        }

        composeTestRule.onNodeWithTag("progressbar").assertIsDisplayed()

        // Now let's update the state to hide the progress bar manually
        ongoingSyncState.value = false

        // Wait for the UI to recompose
        composeTestRule.waitForIdle()
        Thread.sleep(1000) // wait a second

        // Now, the progress bar should be hidden!?
        composeTestRule.onNodeWithTag("progressbar").assertIsNotDisplayed()
    }

    @Test
    fun testBookItemsAreDisplayedWhenThereAreBooks() {
        val testBooks = listOf(
            Book(1, "Book 1", "desc 1","Author 1", "2023",  null),
            Book(2, "Book 2", "desc 2","Author 2", "2022",  null)
        )
        val ongoingSyncState = MutableStateFlow(false)
        composeTestRule.setContent {
            BooksListView(
                ongoingSyncInProgress = ongoingSyncState,
                booksFlow = flowOf(PagingData.from(testBooks)),
                onBookItemClick = {},emptyPlaceholder = {}
            )
        }

        composeTestRule.onNodeWithTag("books-list")
            .onChildren()
            .assertCountEquals(2) // Check if two books are displayed

        // check the title of first item
        val firstItem = composeTestRule.onAllNodesWithTag("book-item")[0]
        firstItem.assertTextContains("Book 1")

        // and the title of the second item
        val secondItem = composeTestRule.onAllNodesWithTag("book-item")[1]
        secondItem.assertTextContains("Book 2")


        // check if second item is below the first item, cause the ordering - probably there are better ways to do this
        val firstItemPosition =firstItem.fetchSemanticsNode().positionInRoot
        val secondItemPosition = secondItem.fetchSemanticsNode().positionInRoot
        assertTrue(secondItemPosition.y > firstItemPosition.y)
    }

}
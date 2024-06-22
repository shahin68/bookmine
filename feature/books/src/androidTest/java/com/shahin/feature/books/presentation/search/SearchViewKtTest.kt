package com.shahin.feature.books.presentation.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.shahin.feature.books.presentation.ui.theme.BooksPreviewTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test


class SearchViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchView_checkIfInitialStateIsDisplayedAsIntended() {
        val hint = "Search something"
        val query = MutableStateFlow("")
        composeTestRule.setContent {
            BooksPreviewTheme {
                SearchView(
                    query = query,
                    queryHint = hint,
                    onQueryChange = {},
                    onSearch = {},
                    onClearQuery = {})
            }
        }

        // check if text field is displayed
        composeTestRule.onNodeWithTag("text-field").assertIsDisplayed()
        // check if hint is displayed instead
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        // check if search icon is visible
        composeTestRule.onNodeWithContentDescription("search").assertIsDisplayed()
        // check if clear icon is is not visible cause there's no input
        composeTestRule.onNodeWithContentDescription("clear").assertIsNotDisplayed()
        // check if text field isn't focused cause there's no input
        composeTestRule.onNodeWithTag("text-field").assertIsNotFocused()
    }

    @Test
    fun searchView_checkWhenQueryChanges() {
        val hint = "Search something"
        val query = MutableStateFlow("")
        composeTestRule.setContent {
            BooksPreviewTheme {
                SearchView(
                    query = query,
                    queryHint = hint,
                    onQueryChange = { query.value = it },
                    onSearch = {},
                    onClearQuery = {}
                )
            }
        }

        // --- Before changing the query ---
        // check if text field is displayed
        composeTestRule.onNodeWithTag("text-field").assertIsDisplayed()
        // check if hint is displayed instead
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        // check if search icon is visible
        composeTestRule.onNodeWithContentDescription("search").assertIsDisplayed()
        // check if clear icon is is not visible cause there's no input
        composeTestRule.onNodeWithContentDescription("clear").assertIsNotDisplayed()
        // check if text field isn't focused cause there's no input
        composeTestRule.onNodeWithTag("text-field").assertIsNotFocused()


        // --- changing the query ---
        // Type something into the text field - query changed
        val newQuery = "Shahin"
        composeTestRule.onNodeWithTag("text-field").performTextInput(newQuery)


        // --- After changing the query ---
        // check if query state is updated
        composeTestRule.runOnIdle {
            assert(query.value == newQuery)
        }
        // check if text field is displayed
        // check if hint is not visible now
        composeTestRule.onNodeWithText(hint).assertIsNotDisplayed()
        // check if search icon is visible - it's always visible by default
        composeTestRule.onNodeWithContentDescription("search").assertIsDisplayed()
        // check if clear icon is is NOW visible
        composeTestRule.onNodeWithContentDescription("clear").assertIsDisplayed()
        // check if text field is NOW focused
        composeTestRule.onNodeWithTag("text-field").assertIsFocused()
    }

    @Test
    fun searchView_checkStateOnSearchIconClick() {
        val hint = "Search something"
        val query = MutableStateFlow("")
        composeTestRule.setContent {
            BooksPreviewTheme {
                SearchView(
                    query = query,
                    queryHint = hint,
                    onQueryChange = {},
                    onSearch = {},
                    onClearQuery = {})
            }
        }

        // --- Before Clicking the search Icon ---
        // check if text field is displayed
        composeTestRule.onNodeWithTag("text-field").assertIsDisplayed()
        // check if hint is displayed instead
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        // check if search icon is visible
        composeTestRule.onNodeWithContentDescription("search").assertIsDisplayed()
        // check if clear icon is is not visible cause there's no input
        composeTestRule.onNodeWithContentDescription("clear").assertIsNotDisplayed()
        // check if text field isn't focused cause there's no input
        composeTestRule.onNodeWithTag("text-field").assertIsNotFocused()

        // --- Click the search Icon ---
        composeTestRule.onNodeWithContentDescription("search").performClick()

        // --- After Clicking the search Icon ---
        // check state should still reflect an empty string value
        composeTestRule.runOnIdle {
            assert(query.value.isEmpty())
        }
        // hint should still be visible
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        // check if search icon is still visible - it's always visible by default
        composeTestRule.onNodeWithContentDescription("search").assertIsDisplayed()
        // check if clear icon is NOW visible - cause by clicking it we can close the search view
        composeTestRule.onNodeWithContentDescription("clear").assertIsDisplayed()
        // check if text field is NOW focused
        composeTestRule.onNodeWithTag("text-field").assertIsFocused()
    }

    @Test
    fun searchView_testClearButtonClickClearsQuery() {
        val hint = "Type something"
        val query = MutableStateFlow("Shahin")
        composeTestRule.setContent {
            BooksPreviewTheme {
                SearchView(
                    query = query,
                    queryHint = hint,
                    onQueryChange = { query.value = it },
                    onSearch = {},
                    onClearQuery = { query.value = "" }
                )
            }
        }

        // Click the clear button
        composeTestRule.onNodeWithContentDescription("clear").performClick()

        // check query state is cleared
        composeTestRule.runOnIdle {
            assert(query.value.isEmpty())
        }

        // check if text field is displayed
        composeTestRule.onNodeWithTag("text-field").assertIsDisplayed()
        // check if hint is displayed instead
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        // check if search icon is visible
        composeTestRule.onNodeWithContentDescription("search").assertIsDisplayed()
        // check if clear icon is is not visible cause there's no input
        composeTestRule.onNodeWithContentDescription("clear").assertIsNotDisplayed()
        // check if text field isn't focused cause there's no input
        composeTestRule.onNodeWithTag("text-field").assertIsNotFocused()
    }

}
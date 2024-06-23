package com.shahin.feature.books.presentation.books.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.presentation.ui.theme.BooksPreviewTheme
import org.junit.Rule
import org.junit.Test

class BookItemViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBookItemViewContent() {
        val testBook = Book(
            bookId = 1,
            title = "shadow of the erdtree",
            description = "For Mohg, you need to go out of your way to first get to Mohgwyn Palace, which is accessed fastest through Varré's questline. For the fight ..",
            image = "some image",
            placeHolder = null,
            errorImage = null
        )

        composeTestRule.setContent {
            BooksPreviewTheme {
                BookItemView(book = testBook)
            }
        }

        // check title is displayed correctly and its value is the title of the [testBook]
        composeTestRule.onNodeWithTag("title")
            .assertIsDisplayed()
            .assertTextEquals(testBook.title ?: "")

        // check description is displayed correctly and its value is exactly the same as the [testBook] description
        composeTestRule.onNodeWithTag("description")
            .assertIsDisplayed()
            .assertTextContains(testBook.description ?: "") // Partial match is sufficient

        // check if the async image is displayed
        composeTestRule.onNodeWithContentDescription("cover_image_${testBook.bookId}")
            .assertIsDisplayed()

    }


}
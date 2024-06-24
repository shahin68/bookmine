package com.shahin.bookmine.presentation.navigation

import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import com.shahin.bookmine.presentation.MainActivity
import com.shahin.bookmine.presentation.MainViewModel
import com.shahin.feature.books.domain.GetBooksUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * End to end test navigating out of books screen to details screens and back
 */
@HiltAndroidTest
@RunWith(MockitoJUnitRunner::class)
class AppNavHostTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    private lateinit var navController: TestNavHostController

    @Mock
    lateinit var getBooksUseCase: GetBooksUseCase

    @Before
    fun setup() {
        composeTestRule.activity.setContent {
            navController = TestNavHostController(LocalContext.current)
            AppNavHost(MainViewModel(getBooksUseCase))
        }
    }

    @Test
    fun booksScreenIsDisplayed() {
        composeTestRule.onNodeWithTag("books-screen")
            .assertIsDisplayed()
    }

    @Test
    fun navigateToBookDetailsScreen() = runTest {
        Thread.sleep(5000) // wait for some items to load

        composeTestRule.onAllNodesWithTag("books-screen")[0] // first item - whatever it is
            .performClick() // click on the first item

        composeTestRule.onNodeWithTag("book-details-screen")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("books-screen")
            .assertIsNotDisplayed()


        // wait for 1 second and then navigate back
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("back-button-container").performClick()

        composeTestRule.onNodeWithTag("book-details-screen")
            .assertIsNotDisplayed()

        composeTestRule.onNodeWithTag("books-screen")
            .assertIsDisplayed()
    }

}
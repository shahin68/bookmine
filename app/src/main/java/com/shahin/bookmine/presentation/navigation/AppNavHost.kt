package com.shahin.bookmine.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shahin.bookmine.R
import com.shahin.bookmine.presentation.MainViewModel
import com.shahin.bookmine.presentation.ui.theme.BookMineTheme
import com.shahin.bookmine.presentation.ui.theme.PaddingLarge
import com.shahin.bookmine.presentation.ui.theme.PaddingMedium
import com.shahin.feature.book_details.presentation.BookDetailsScreen
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.presentation.books.BooksScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

const val TRANSITION_DURATION_MILLIS = 700

@Composable
fun AppNavHost(mainViewModel: MainViewModel = hiltViewModel()) {

    val navController = rememberNavController()

    val animationSpec = tween<Float>(TRANSITION_DURATION_MILLIS)

    NavHost(
        navController = navController,
        startDestination = ScreenBooks,
        enterTransition = { fadeIn(animationSpec = animationSpec) },
        exitTransition = { fadeOut(animationSpec = animationSpec) }
    ) {

        composable<ScreenBooks> {
            BooksScreen(
                ongoingSyncInProgress = mainViewModel.ongoingSyncInProgress,
                onBookItemClick = { book ->
                    navController.navigate(
                        ScreenBookDetails(
                            bookId = book.bookId
                        )
                    )
                }
            )
        }

        composable<ScreenBookDetails> {
            val args = it.toRoute<ScreenBookDetails>()
            BooksDetailsScreen(
                screenBookDetails = args,
                waitingForTransition = animationSpec.durationMillis.toLong(),
                onClosed = {
                    navController.navigateUp()
                }
            )
        }

    }
}

@Composable
private fun BooksScreen(
    modifier: Modifier = Modifier,
    ongoingSyncInProgress: StateFlow<Boolean>,
    onBookItemClick: (Book) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("books-screen")
    ) {
        BooksScreen(
            ongoingSyncInProgress = ongoingSyncInProgress,
            itemPlaceHolder = R.drawable.placeholder,
            itemErrorImage = R.drawable.error_item,
            queryHint = stringResource(R.string.search_for_titles),
            onBookItemClick = onBookItemClick,
            emptyPlaceholder = {
                EmptyBooksPlaceholder()
            }
        )
    }
}

@Composable
private fun EmptyBooksPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(PaddingLarge)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        Image(
            modifier = Modifier.weight(1f),
            painter = painterResource(id = R.drawable.empty_list),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            contentDescription = "empty-list-image"
        )
        Text(
            modifier = Modifier.testTag("empty-list-message"),
            text = stringResource(R.string.oops_nothing_to_show_for_now),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BooksDetailsScreen(
    modifier: Modifier = Modifier,
    screenBookDetails: ScreenBookDetails,
    waitingForTransition: Long,
    onClosed: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    // edge case for when the action onClose is triggered before the navigation transition is completed
    LaunchedEffect(Unit) {
        delay(waitingForTransition)
        isLoading = false
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("book-details-screen")
    ) {
        BookDetailsScreen(
            headerImagePlaceholder = R.drawable.placeholder,
            headerImageError = R.drawable.error_header,
            bookId = screenBookDetails.bookId,
            onClose = {
                if (!isLoading) onClosed()
            }
        )
    }
}

@Preview
@Composable
private fun AppNavHostPreview() {
    BookMineTheme {
        AppNavHost()
    }
}
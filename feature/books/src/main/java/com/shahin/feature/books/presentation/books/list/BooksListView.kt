package com.shahin.feature.books.presentation.books.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.shahin.feature.books.BuildConfig
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.presentation.ui.theme.CornerRadiusExtraExtraSmall
import com.shahin.feature.books.presentation.ui.theme.CornerRadiusMedium
import com.shahin.feature.books.presentation.ui.theme.PaddingExtraSmall
import com.shahin.feature.books.presentation.ui.theme.PaddingLarge
import com.shahin.feature.books.presentation.ui.theme.PaddingMedium
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BooksListView(
    modifier: Modifier = Modifier,
    ongoingSyncInProgress: StateFlow<Boolean>,
    booksFlow: Flow<PagingData<Book>>,
    onBookItemClick: (Book) -> Unit,
    emptyPlaceholder: @Composable () -> Unit,
) {

    val books = booksFlow.collectAsLazyPagingItems()

    ProgressBarView(
        books = books,
        ongoingSyncInProgress = ongoingSyncInProgress
    )

    Box(
        modifier = modifier
            .imePadding()
            .padding(PaddingMedium)
            .clip(RoundedCornerShape(CornerRadiusMedium))
    ) {
        if (books.itemCount == 0) {
            EmptyPlaceHolderView {
                emptyPlaceholder()
            }
        }
        LazyColumn(
            modifier = Modifier.testTag("books-list"),
            verticalArrangement = Arrangement.spacedBy(PaddingExtraSmall)
        ) {
            items(
                count = books.itemCount,
                key = books.itemKey { book ->
                    "${book.bookId}${book.author}"
                },
                contentType = books.itemContentType { book ->
                    "${book.bookId}${book.releaseDate}"
                }
            ) { index ->
                val book = books[index] ?: return@items
                val isFirstItem = index == 0
                val isLastItem = index == books.itemCount - 1
                BookItemView(
                    modifier = Modifier
                        .then(
                            if (BuildConfig.DEBUG) {
                                Modifier.animateItemPlacement()
                            } else {
                                Modifier
                            }
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = if (isFirstItem) CornerRadiusMedium else CornerRadiusExtraExtraSmall,
                                topEnd = if (isFirstItem) CornerRadiusMedium else CornerRadiusExtraExtraSmall,
                                bottomStart = if (isLastItem) CornerRadiusMedium else CornerRadiusExtraExtraSmall,
                                bottomEnd = if (isLastItem) CornerRadiusMedium else CornerRadiusExtraExtraSmall
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable {
                            onBookItemClick(book)
                        }
                        .padding(PaddingMedium)
                        .testTag("book-item"),
                    book = book
                )
            }
        }
    }
}

@Composable
private fun EmptyPlaceHolderView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
            .fillMaxSize()
            .testTag("placeholder")
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
        ) {
            content()
        }
    }
}

@Composable
private fun ProgressBarView(
    modifier: Modifier = Modifier,
    books: LazyPagingItems<Book>,
    ongoingSyncInProgress: StateFlow<Boolean>,
) {
    val synOngoing by ongoingSyncInProgress.collectAsStateWithLifecycle()
    AnimatedVisibility(
        modifier = modifier,
        visible = books.loadState.refresh == LoadState.Loading || synOngoing,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingMedium)
                .padding(top = PaddingExtraSmall)
                .testTag("progressbar"),
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview
@Composable
private fun BooksListViewPreview() {
    val dummyBooksList = mutableListOf<Book>()
    repeat(10) { index ->
        dummyBooksList.add(
            Book(
                bookId = index.toLong(),
                title = "title $index",
                description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                author = "author",
                releaseDate = "releaseDate",
                image = "https://avatars.githubusercontent.com/u/18089142?v=4"
            )
        )
    }
    BooksListView(
        ongoingSyncInProgress = MutableStateFlow(true),
        booksFlow = flowOf(PagingData.from(dummyBooksList)),
        onBookItemClick = {},
        emptyPlaceholder = {
            Text(text = "Oops... nothing to show")
        }
    )
}

@Preview
@Composable
private fun BooksListViewPreviewEmptyList() {
    BooksListView(
        ongoingSyncInProgress = MutableStateFlow(true),
        booksFlow = flowOf(PagingData.from(emptyList())),
        onBookItemClick = {},
        emptyPlaceholder = {
            Column(
                modifier = Modifier.padding(PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PaddingMedium)
            ) {
                Image(imageVector = Icons.Rounded.Warning, contentDescription = "warning-icon")
                Text(text = "Oops... nothing to show")
            }
        }
    )
}
package com.shahin.feature.books.presentation.books

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.map
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.presentation.books.list.BooksListView
import com.shahin.feature.books.presentation.search.SearchView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Composable
fun BooksScreen(
    modifier: Modifier = Modifier,
    booksViewModel: BooksViewModel = hiltViewModel(),
    ongoingSyncInProgress: StateFlow<Boolean>,
    @DrawableRes itemPlaceHolder: Int? = null,
    @DrawableRes itemErrorImage: Int? = null,
    queryHint: String,
    onBookItemClick: (Book) -> Unit,
    emptyPlaceholder: @Composable () -> Unit
) {

    LaunchedEffect(Unit) {
        booksViewModel.searchQuery.collectLatest { query ->
            booksViewModel.getBooks(query = query)
        }
    }

    val booksFlow = booksViewModel.books.map { pagingData ->
        pagingData.map { book ->
            book.copy(placeHolder = itemPlaceHolder, errorImage = itemErrorImage)
        }
    }

    HomeScreenContent(
        modifier = modifier,
        ongoingSyncInProgress = ongoingSyncInProgress,
        booksFlow = booksFlow,
        searchQuery = booksViewModel.searchQuery,
        queryHint = queryHint,
        onQueryChange = booksViewModel::onSearchQueryChanged,
        onClearQuery = booksViewModel::clearSearchQuery,
        onBookItemClick = onBookItemClick,
        emptyPlaceholder = emptyPlaceholder
    )

}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    ongoingSyncInProgress: StateFlow<Boolean>,
    booksFlow: Flow<PagingData<Book>>,
    searchQuery: StateFlow<String>,
    queryHint: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onBookItemClick: (Book) -> Unit,
    emptyPlaceholder: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.navigationBarsPadding(),
        topBar = {
            SearchView(
                modifier = Modifier,
                query = searchQuery,
                queryHint = queryHint,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            BooksListView(
                ongoingSyncInProgress = ongoingSyncInProgress,
                booksFlow = booksFlow,
                onBookItemClick = onBookItemClick,
                emptyPlaceholder = emptyPlaceholder
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
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
    HomeScreenContent(
        booksFlow = flowOf(PagingData.from(dummyBooksList)),
        searchQuery = MutableStateFlow(""),
        queryHint = "Search by title ...",
        onQueryChange = {},
        onClearQuery = {},
        onBookItemClick = {},
        emptyPlaceholder = {},
        ongoingSyncInProgress = MutableStateFlow(true)
    )
}

@Preview
@Composable
private fun HomeScreenPreviewEmpty() {
    HomeScreenContent(
        booksFlow = flowOf(PagingData.from(emptyList())),
        searchQuery = MutableStateFlow(""),
        queryHint = "Search by title ...",
        onQueryChange = {},
        onClearQuery = {},
        onBookItemClick = {},
        emptyPlaceholder = {
            Text(text = "Whatever empty placeholder ...")
        },
        ongoingSyncInProgress = MutableStateFlow(true)
    )
}
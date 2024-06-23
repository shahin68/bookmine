package com.shahin.feature.books.presentation.books.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import coil.compose.AsyncImage
import com.shahin.feature.books.R
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.presentation.ui.theme.BookItemImageSize
import com.shahin.feature.books.presentation.ui.theme.BooksPreviewTheme
import com.shahin.feature.books.presentation.ui.theme.PaddingMedium

@Composable
fun BookItemView(
    modifier: Modifier = Modifier,
    book: Book
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(BookItemImageSize)
                .clip(CircleShape)
            ,
            model = book.image,
            contentScale = ContentScale.Crop,
            contentDescription = "cover_image_${book.bookId}",
            placeholder = book.placeHolder?.let { painterResource(id = it) },
            error = book.errorImage?.let { painterResource(id = it) }
        )
        Column {
            Text(
                modifier = Modifier.testTag("title"),
                text = book.title ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier.testTag("description"),
                text = book.description ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@PreviewFontScale
@Composable
private fun BookItemViewPreview() {
    val book = Book(
        bookId = 1,
        title = "title",
        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        author = "author",
        releaseDate = "releaseDate",
        image = "https://avatars.githubusercontent.com/u/18089142?v=4",
        placeHolder = R.drawable.placeholder_preview
    )
    BooksPreviewTheme {
        BookItemView(
            book = book
        )
    }
}
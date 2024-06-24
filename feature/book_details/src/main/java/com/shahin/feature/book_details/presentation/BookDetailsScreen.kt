package com.shahin.feature.book_details.presentation

import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.shahin.feature.book_details.R
import com.shahin.feature.book_details.data.model.BookDetails
import com.shahin.feature.book_details.presentation.ui.theme.BackButtonSize
import com.shahin.feature.book_details.presentation.ui.theme.BooksDetailsPreviewTheme
import com.shahin.feature.book_details.presentation.ui.theme.ButtonSizeSmall
import com.shahin.feature.book_details.presentation.ui.theme.CornerRadiusLarge
import com.shahin.feature.book_details.presentation.ui.theme.CornerRadiusMedium
import com.shahin.feature.book_details.presentation.ui.theme.ElevationSmall
import com.shahin.feature.book_details.presentation.ui.theme.HeaderImageSize
import com.shahin.feature.book_details.presentation.ui.theme.PaddingMedium
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

@Composable
fun BookDetailsScreen(
    modifier: Modifier = Modifier,
    bookId: Long,
    @DrawableRes headerImagePlaceholder: Int? = null,
    @DrawableRes headerImageError: Int? = null,
    bookDetailsViewModel: BookDetailsViewModel = hiltViewModel(),
    onClose: () -> Unit,
) {

    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.addCallback(
        LocalLifecycleOwner.current,
        enabled = true
    ) {
        onClose()
    }

    val bookDetails by bookDetailsViewModel.book.map { book ->
        book?.copy(placeHolder = headerImagePlaceholder, errorImage = headerImageError)
    }.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(bookId) {
        bookDetailsViewModel.getBookById(bookId = bookId)
    }


    DetailsScreenAppBar(modifier = modifier, bookDetails = bookDetails, onClose = onClose)
}

@Composable
private fun DetailsScreenAppBar(
    modifier: Modifier = Modifier,
    bookDetails: BookDetails?,
    onClose: () -> Unit,
    headerImageSize: Dp = HeaderImageSize,
    backButtonSize: Dp = ButtonSizeSmall,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .testTag("parent-column-container")
            .verticalScroll(scrollState, enabled = true)
            .navigationBarsPadding()
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        bottomEnd = CornerRadiusLarge,
                        bottomStart = CornerRadiusLarge
                    )
                )
                .fillMaxWidth()
                .height(headerImageSize),
            model = bookDetails?.image,
            contentScale = ContentScale.Crop,
            contentDescription = "cover-image",
            placeholder = bookDetails?.placeHolder?.let { painterResource(id = it) },
            error = bookDetails?.errorImage?.let { painterResource(id = it) }
        )
        DetailsScreenContent(
            modifier = Modifier
                .padding(PaddingMedium)
                .testTag("details-container"),
            bookDetails = bookDetails
        )
    }

    HeaderView(
        bookDetails = bookDetails,
        scrollState = scrollState,
        headerImageSize = headerImageSize,
        backButtonSize = backButtonSize
    ) { buttonSize ->
        BackButtonView(
            modifier = modifier
                .statusBarsPadding()
                .padding(PaddingMedium)
                .size(buttonSize),
            onClose = onClose
        )
    }
}

@Composable
private fun BackButtonView(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    FilledTonalIconButton(
        modifier = modifier.testTag("back-button-container"),
        onClick = onClose
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "back-button"
        )
    }
}

@Composable
private fun HeaderView(
    modifier: Modifier = Modifier,
    bookDetails: BookDetails?,
    scrollState: ScrollState,
    headerImageSize: Dp,
    backButtonSize: Dp,
    backButton: @Composable (BackButtonSize) -> Unit,
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box {
        TopHeaderTitle(
            modifier = modifier,
            bookDetails = bookDetails,
            scrollState = scrollState,
            headerRevealThresholdDp = headerImageSize - statusBarHeight - PaddingMedium,
            backButtonSize = backButtonSize
        )
        backButton(backButtonSize)
    }
}

@Composable
private fun TopHeaderTitle(
    modifier: Modifier = Modifier,
    bookDetails: BookDetails?,
    scrollState: ScrollState,
    headerRevealThresholdDp: Dp,
    backButtonSize: Dp,
) {
    var showColumn by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current
    val cornerRadiusSize = CornerRadiusMedium
    val paddingSize = PaddingMedium

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collectLatest { position ->
                showColumn = position > with(density) { headerRevealThresholdDp.toPx() }
            }
    }

    AnimatedVisibility(
        visible = showColumn,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Row(
            modifier = modifier
                .shadow(
                    ElevationSmall,
                    RoundedCornerShape(bottomEnd = cornerRadiusSize, bottomStart = cornerRadiusSize)
                )
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(
                        bottomEnd = cornerRadiusSize,
                        bottomStart = cornerRadiusSize
                    )
                )
                .statusBarsPadding()
                .fillMaxWidth()
        ) {
            Spacer(
                modifier = Modifier
                    .padding(paddingSize)
                    .size(backButtonSize)
            )
            Column(
                modifier = Modifier
                    .padding(vertical = paddingSize)
                    .padding(end = paddingSize)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.testTag("header-title"),
                    text = bookDetails?.title.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("header-release-date"),
                    text = bookDetails?.releaseDate.orEmpty(),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }

}

@Composable
private fun DetailsScreenContent(
    modifier: Modifier = Modifier,
    bookDetails: BookDetails?,
) {
    val paddingSize = PaddingMedium
    val boxModifier = Modifier
        .clip(RoundedCornerShape(CornerRadiusMedium))
        .background(
            color = MaterialTheme.colorScheme.surfaceContainer,
        )
        .padding(paddingSize)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(paddingSize)
    ) {
        Column(
            modifier = boxModifier
        ) {
            Text(
                modifier = Modifier.testTag("details-title"),
                text = bookDetails?.title.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("details-release-date"),
                text = bookDetails?.releaseDate.orEmpty(),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                modifier = Modifier
                    .padding(top = paddingSize)
                    .testTag("details-description"),
                text = bookDetails?.description.orEmpty(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Box(
            modifier = boxModifier
        ) {
            Text(
                modifier = Modifier.testTag("details-author"),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Author:")
                    }
                    append(" ")
                    append(bookDetails?.author.orEmpty())
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showSystemUi = true)
@PreviewFontScale
@Composable
private fun DetailsScreenAppBarPreview() {
    val book = BookDetails(
        bookId = 1,
        title = "RADAGON OF THE GOLDEN ORDER",
        description = "Radagon is a tall, fractured god wielding the hammer that shattered the Elden Ring, and is found in the Elden Throne. He was previously married to Rennala, but became Queen Marika's consort after Godfrey was exiled from the Lands Between. Completing Brother Corhyn & Goldmask's quest to a certain point reveals that he is the \"other half\" of Queen Marika. Despite being of one body, the two do not share the same will, as evident from Radagon attempting to repair the Elden Ring after Marika had shattered it.\n" +
                "\n" +
                "This boss is NOT optional as players must defeat him to reach the end of Elden Ring. As with many endgame bosses, Weapons, Incantations, and Spirit Ashes capable of inflicting Black Flame are highly recommended as they deal damage over time based on the boss' maximum HP. Be aware that depleting his health completely immediately takes you into the cutscene leading to the final boss fight - Elden Beast. If you die to the Elden Beast, you are required to fight Radagon again before reaching it.",
        author = "ELDEN RING WIKI",
        releaseDate = "2022",
        image = "",
        placeHolder = R.drawable.placeholder_preview,
        errorImage = R.drawable.placeholder_preview,
    )
    BooksDetailsPreviewTheme {
        DetailsScreenAppBar(
            bookDetails = book,
            onClose = {}
        )
    }
}
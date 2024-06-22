package com.shahin.feature.books.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.shahin.feature.books.presentation.ui.theme.BooksPreviewTheme
import com.shahin.feature.books.presentation.ui.theme.CornerRadiusSmall
import com.shahin.feature.books.presentation.ui.theme.PaddingExtraSmall
import com.shahin.feature.books.presentation.ui.theme.PaddingMedium
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    query: StateFlow<String>,
    queryHint: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    val queryInput by query.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused: Boolean by remember { mutableStateOf(false) }
    val animatedFloatFactor by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        label = "floatFactor"
    )
    val closeIconTransformOrigin = TransformOrigin(0.5f, 0.5f)
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val contentColor = MaterialTheme.colorScheme.onSurface
    val contentSelectionColor = MaterialTheme.colorScheme.surfaceContainerLowest

    TextField(
        modifier = modifier
            .testTag("text-field")
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .fillMaxWidth()
            .padding(top = PaddingExtraSmall)
            .padding(horizontal = PaddingMedium.times(1 - animatedFloatFactor))
            .statusBarsPadding()
            .drawBehind {
                if (size.height == 0f || size.width == 0f) return@drawBehind // in case the parent view size is 0
                drawRoundRect(
                    color = containerColor,
                    topLeft = Offset(x = 0f, y = -(size.height.times(animatedFloatFactor).times(2))),
                    cornerRadius = CornerRadius(CornerRadiusSmall.times(1 - animatedFloatFactor).toPx())
                )
            },
        value = queryInput,
        onValueChange = onQueryChange,
        leadingIcon = {
            IconButton(
                onClick = {
                    focusRequester.requestFocus()
                    if (isFocused) {
                        keyboardController?.show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "search",
                    tint = contentColor
                )
            }
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = isFocused || queryInput.isNotEmpty(),
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    transformOrigin = closeIconTransformOrigin
                ),
                exit = scaleOut(transformOrigin = closeIconTransformOrigin)
            ) {
                IconButton(
                    onClick = {
                        if (isFocused) focusManager.clearFocus()
                        if (queryInput.isNotEmpty()) onClearQuery()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "clear",
                        tint = contentColor
                    )
                }
            }
        },
        placeholder = {
            Text(
                modifier = Modifier.testTag("placeholder"),
                text = queryHint,
                color = contentColor.copy(alpha = 0.5f)
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = contentColor,
            focusedTextColor = contentColor,
            disabledTextColor = contentColor.copy(alpha = 0.65f),
            unfocusedTrailingIconColor = contentColor,
            focusedTrailingIconColor = contentColor,
            cursorColor = contentColor,
            selectionColors = TextSelectionColors(
                handleColor = contentColor,
                backgroundColor = contentSelectionColor
            )
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (isFocused) focusManager.clearFocus()
                if (queryInput.isBlank()) onClearQuery()
            }
        )
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun SearchViewEmptyPreview() {
    BooksPreviewTheme {
        SearchView(
            query = MutableStateFlow(""),
            onQueryChange = {},
            onClearQuery = {},
            queryHint = "Search by title ..."
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun SearchViewFilledPreview() {
    BooksPreviewTheme {
        SearchView(
            query = MutableStateFlow("Some title"),
            onQueryChange = {},
            onClearQuery = {},
            queryHint = "Search by title ..."
        )
    }
}

package com.shahin.bookmine.presentation.error

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shahin.bookmine.R
import com.shahin.bookmine.presentation.MainViewModel
import com.shahin.core.network.model.NetworkResponse

@Composable
fun IsolatedErrorHandling(viewModel: MainViewModel = hiltViewModel()) {
    val snackBarHostState = remember { SnackbarHostState() }
    val errorResponse by viewModel.errorResponse.collectAsStateWithLifecycle()
    val retryCount by viewModel.retryCount.collectAsStateWithLifecycle()

    val shouldShowErrorMessage by remember {
        derivedStateOf {
            retryCount == 3 && errorResponse != null
        }
    }

    val context = LocalContext.current

    LaunchedEffect(shouldShowErrorMessage) {
        errorResponse?.let { error ->
            snackBarHostState.showSnackbar(
                message = when (error) {
                    is NetworkResponse.ClientError -> {
                        context.getString(R.string.failed_to_sync_you_can_contact_support)
                    }

                    is NetworkResponse.NetworkError -> {
                        context.getString(R.string.please_check_your_internet_connection)
                    }

                    is NetworkResponse.ServerError -> {
                        context.getString(R.string.something_went_wrong_please_try_again_later)
                    }

                    else -> {
                        ""
                    }
                },
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorResponse()
        }
    }

    LaunchedEffect(retryCount) {
        if (retryCount == 3)
            viewModel.onSyncComplete()
    }

    SnackBarView(hostState = snackBarHostState)
}

@Composable
private fun SnackBarView(modifier: Modifier = Modifier, hostState: SnackbarHostState) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = hostState
        )
    }
}
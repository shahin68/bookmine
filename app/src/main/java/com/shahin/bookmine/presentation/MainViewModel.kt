package com.shahin.bookmine.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahin.bookmine.BuildConfig
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.model.NetworkResponse
import com.shahin.feature.books.domain.GetBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
) : ViewModel() {

    private val _errorResponse = MutableStateFlow<NetworkResponse<List<BookItem?>>?>(null)
    val errorResponse: StateFlow<NetworkResponse<List<BookItem?>>?> get() = _errorResponse

    /**
     * Not allowing this state to be nullable for the reason of adding an understanding
     * to the logics of error handling
     * we really wanna show that we want our retry count to go up to 3 and that's it
     * and then we can check on UI leve if it is 3, we have no more retry attempts
     *
     * Making this state nullable adds a bit more complexity to how we should handle the act of retrying
     */
    private val _retryCount = MutableStateFlow(0)
    val retryCount: StateFlow<Int> get() = _retryCount

    private val _ongoingSyncInProgress = MutableStateFlow(false)
    val ongoingSyncInProgress: StateFlow<Boolean> get() = _ongoingSyncInProgress

    init {
        syncBooks()
    }

    private fun syncBooks() {
        _ongoingSyncInProgress.value = true
        viewModelScope.launch {
            syncBooksUseCase()
            repeat(3) {
                delay(2000)
                if (_errorResponse.value != null) {
                    clearErrorResponse()
                    _retryCount.value += 1
                    syncBooksUseCase()
                }
            }
        }
    }

    fun onSyncComplete() {
        _ongoingSyncInProgress.value = false
        clearErrorResponse()
    }

    fun clearErrorResponse() {
        _errorResponse.value = null
    }

    private suspend fun syncBooksUseCase() {
        when (val response = getBooksUseCase.syncBooks(BuildConfig.MOCKY_IDENTIFIER)) {
            is NetworkResponse.Success -> {
                _retryCount.value = 3
                // no additional actions
            }
            else -> {
                _errorResponse.value = response
            }
        }
    }

}
package com.shahin.feature.book_details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahin.feature.book_details.data.model.BookDetails
import com.shahin.feature.book_details.domain.GetBookByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val getBookByIdUseCase: GetBookByIdUseCase
): ViewModel() {

    private val _book = MutableStateFlow<BookDetails?>(null)
    val book: StateFlow<BookDetails?> = _book

    fun getBookById(bookId: Long) {
        viewModelScope.launch {
            getBookByIdUseCase.getBookById(bookId).collectLatest {
                _book.value = it
            }
        }
    }

}
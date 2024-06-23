package com.shahin.feature.books.presentation.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shahin.feature.books.data.model.Book
import com.shahin.feature.books.domain.GetBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String>
        get() = _searchQuery

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        onSearchQueryChanged("")
    }

    private val _books = MutableStateFlow<PagingData<Book>>(PagingData.empty())
    val books: StateFlow<PagingData<Book>> get() = _books

    fun getBooks(query: String) {
        viewModelScope.launch {
            getBooksUseCase.getLocalBooks(titleQuery = query).cachedIn(viewModelScope).collectLatest {
                _books.value = it
            }
        }
    }

}
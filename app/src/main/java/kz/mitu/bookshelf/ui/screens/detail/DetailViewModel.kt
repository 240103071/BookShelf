package kz.mitu.bookshelf.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.BookShelfApp
import kz.mitu.bookshelf.navigation.Screen

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val bookId: Int = checkNotNull(savedStateHandle[Screen.ARG_BOOK_ID]) {
        "DetailViewModel requires a \"${Screen.ARG_BOOK_ID}\" argument"
    }

    private val repository = BookShelfApp.instance.bookRepository

    val book: StateFlow<Book?> = repository.getBook(bookId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun changeStatus(status: BookStatus) {
        viewModelScope.launch { repository.updateStatus(bookId, status) }
    }

    fun updateRating(rating: Int) {
        val current = book.value ?: return
        viewModelScope.launch { repository.updateBook(current.copy(rating = rating.coerceIn(0, 5))) }
    }

    fun deleteBook() {
        viewModelScope.launch { repository.deleteBook(bookId) }
    }
}

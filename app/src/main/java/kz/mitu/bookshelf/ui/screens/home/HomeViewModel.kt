package kz.mitu.bookshelf.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.repository.BookRepository

data class HomeUiState(
    val books: List<Book> = emptyList(),
    val query: String = "",
    val selectedStatus: BookStatus? = null
)

class HomeViewModel : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<BookStatus?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        BookRepository.books,
        query,
        selectedStatus
    ) { books, query, status ->
        val trimmed = query.trim()
        val filtered = books.filter { book ->
            val matchesQuery = trimmed.isEmpty() ||
                book.title.contains(trimmed, ignoreCase = true) ||
                book.author.contains(trimmed, ignoreCase = true)
            val matchesStatus = status == null || book.status == status
            matchesQuery && matchesStatus
        }
        HomeUiState(books = filtered, query = query, selectedStatus = status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onStatusFilterChange(status: BookStatus?) {
        selectedStatus.value = status
    }
}

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
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.data.repository.BookRepository

data class HomeUiState(
    val books: List<Book> = emptyList(),
    val query: String = "",
    val selectedStatus: BookStatus? = null,
    val selectedSort: SortOption = SortOption.RECENTLY_ADDED,
    /** Distinct genres of all books (not only the filtered ones), A–Z. */
    val genres: List<String> = emptyList()
)

class HomeViewModel : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<BookStatus?>(null)
    private val selectedSort = MutableStateFlow(SortOption.RECENTLY_ADDED)

    val uiState: StateFlow<HomeUiState> = combine(
        BookRepository.books,
        query,
        selectedStatus,
        selectedSort
    ) { books, query, status, sort ->
        val trimmed = query.trim()
        val filtered = books.filter { book ->
            val matchesQuery = trimmed.isEmpty() ||
                book.title.contains(trimmed, ignoreCase = true) ||
                book.author.contains(trimmed, ignoreCase = true)
            val matchesStatus = status == null || book.status == status
            matchesQuery && matchesStatus
        }
        HomeUiState(
            books = filtered.sortedWith(sort.comparator()),
            query = query,
            selectedStatus = status,
            selectedSort = sort,
            genres = books.toGenres()
        )
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

    fun onSortChange(sort: SortOption) {
        selectedSort.value = sort
    }

    private fun List<Book>.toGenres(): List<String> =
        map { it.genre.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)

    private fun SortOption.comparator(): Comparator<Book> = when (this) {
        SortOption.TITLE -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }
        SortOption.RATING -> compareByDescending<Book> { it.rating }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.title }
        SortOption.RECENTLY_ADDED -> compareByDescending { it.id }
    }
}

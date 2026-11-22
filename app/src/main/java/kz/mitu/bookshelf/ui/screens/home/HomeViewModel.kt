package kz.mitu.bookshelf.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.BookShelfApp
import kz.mitu.bookshelf.data.repository.BookRepository
import kz.mitu.bookshelf.data.settings.SettingsRepository

data class HomeUiState(
    val books: List<Book> = emptyList(),
    val query: String = "",
    val selectedStatus: BookStatus? = null,
    val selectedSort: SortOption = SortOption.RECENTLY_ADDED,
    val selectedGenre: String? = null,
    val genres: List<String> = emptyList()
)

class HomeViewModel(
    private val repository: BookRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<BookStatus?>(null)
    private val selectedGenre = MutableStateFlow<String?>(null)

    private val filters = combine(query, selectedStatus, selectedGenre) { query, status, genre ->
        Triple(query, status, genre)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repository.books,
        filters,
        settingsRepository.sortOption
    ) { books, (query, status, genre), sort ->
        val trimmed = query.trim()
        val filtered = books.filter { book ->
            val matchesQuery = trimmed.isEmpty() ||
                book.title.contains(trimmed, ignoreCase = true) ||
                book.author.contains(trimmed, ignoreCase = true)
            val matchesStatus = status == null || book.status == status
            val matchesGenre = genre == null || book.genre.trim().equals(genre.trim(), ignoreCase = true)
            matchesQuery && matchesStatus && matchesGenre
        }
        HomeUiState(
            books = filtered.sortedWith(sort.comparator()),
            query = query,
            selectedStatus = status,
            selectedSort = sort,
            selectedGenre = genre,
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
        viewModelScope.launch { settingsRepository.setSortOption(sort) }
    }

    fun onGenreChange(genre: String?) {
        selectedGenre.value = genre
    }

    fun clearFilters() {
        query.value = ""
        selectedStatus.value = null
        selectedGenre.value = null
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookShelfApp
                HomeViewModel(app.bookRepository, app.settingsRepository)
            }
        }
    }
}

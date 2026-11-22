package kz.mitu.bookshelf.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.BookShelfApp

data class StatsUiState(
    val totalBooks: Int = 0,
    val countByStatus: Map<BookStatus, Int> = BookStatus.entries.associateWith { 0 },
    val pagesRead: Int = 0,
    val averageRating: Float = 0f
)

class StatsViewModel : ViewModel() {
    private val repository = BookShelfApp.instance.bookRepository

    val uiState: StateFlow<StatsUiState> = repository.books
        .map(::toStats)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState()
        )

    private fun toStats(books: List<Book>): StatsUiState {
        val ratings = books.map { it.rating }.filter { it > 0 }
        return StatsUiState(
            totalBooks = books.size,
            countByStatus = BookStatus.entries.associateWith { status ->
                books.count { it.status == status }
            },
            pagesRead = books.filter { it.status == BookStatus.FINISHED }.sumOf { it.pages },
            averageRating = if (ratings.isEmpty()) 0f else ratings.average().toFloat()
        )
    }
}

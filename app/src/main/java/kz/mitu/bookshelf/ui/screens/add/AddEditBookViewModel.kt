package kz.mitu.bookshelf.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.BookShelfApp
import kz.mitu.bookshelf.navigation.Screen

data class AddEditUiState(
    val isEditMode: Boolean = false,
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val pages: String = "",
    val rating: Int = 0,
    val status: BookStatus = BookStatus.WANT_TO_READ,
    val description: String = "",
    val titleError: Boolean = false,
    val authorError: Boolean = false,
    val pagesError: Boolean = false
)

class AddEditBookViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val bookId: Int? = savedStateHandle[Screen.ARG_BOOK_ID]

    private val repository = BookShelfApp.instance.bookRepository

    private val _uiState = MutableStateFlow(AddEditUiState(isEditMode = bookId != null))
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (bookId != null) {
            viewModelScope.launch {
                val book = repository.getBook(bookId).first() ?: return@launch
                _uiState.update {
                    it.copy(
                        title = book.title,
                        author = book.author,
                        genre = book.genre,
                        pages = book.pages.toString(),
                        rating = book.rating,
                        status = book.status,
                        description = book.description
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, titleError = false) }

    fun onAuthorChange(value: String) =
        _uiState.update { it.copy(author = value, authorError = false) }

    fun onGenreChange(value: String) =
        _uiState.update { it.copy(genre = value) }

    fun onPagesChange(value: String) =
        _uiState.update { it.copy(pages = value, pagesError = false) }

    fun onRatingChange(value: Int) =
        _uiState.update { it.copy(rating = value.coerceIn(0, 5)) }

    fun onStatusChange(value: BookStatus) =
        _uiState.update { it.copy(status = value) }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value) }

    fun save(): Boolean {
        val state = _uiState.value
        val pages = state.pages.trim().toIntOrNull()
        val titleError = state.title.isBlank()
        val authorError = state.author.isBlank()
        val pagesError = pages == null || pages <= 0

        if (titleError || authorError || pagesError) {
            _uiState.update {
                it.copy(titleError = titleError, authorError = authorError, pagesError = pagesError)
            }
            return false
        }

        val book = Book(
            id = bookId ?: 0,
            title = state.title.trim(),
            author = state.author.trim(),
            genre = state.genre.trim(),
            pages = pages!!,
            rating = state.rating,
            status = state.status,
            description = state.description.trim()
        )
        viewModelScope.launch {
            if (state.isEditMode) repository.updateBook(book) else repository.addBook(book)
        }
        return true
    }
}

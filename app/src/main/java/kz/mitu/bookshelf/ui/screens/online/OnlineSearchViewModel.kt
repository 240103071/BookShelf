package kz.mitu.bookshelf.ui.screens.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.mitu.bookshelf.data.model.OnlineBook
import kz.mitu.bookshelf.data.remote.RetrofitClient
import kz.mitu.bookshelf.data.repository.OnlineBookRepository
import retrofit2.HttpException

const val DEFAULT_ONLINE_QUERY = "Dostoevsky"

enum class NetworkError { NO_INTERNET, SERVER, UNKNOWN }

sealed interface SearchResult {
    data object Loading : SearchResult
    data class Success(val books: List<OnlineBook>) : SearchResult
    data object Empty : SearchResult
    data class Error(val error: NetworkError) : SearchResult
}

data class OnlineSearchUiState(
    val query: String = DEFAULT_ONLINE_QUERY,
    val result: SearchResult = SearchResult.Loading
)

class OnlineSearchViewModel(
    private val onlineRepository: OnlineBookRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnlineSearchUiState())
    val uiState: StateFlow<OnlineSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        search()
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search() {
        val query = _uiState.value.query.trim()
        searchJob?.cancel()
        if (query.isEmpty()) {
            _uiState.update { it.copy(result = SearchResult.Empty) }
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(result = SearchResult.Loading) }
            val result = try {
                val books = onlineRepository.searchBooks(query)
                if (books.isEmpty()) SearchResult.Empty else SearchResult.Success(books)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                SearchResult.Error(e.toNetworkError())
            }
            _uiState.update { it.copy(result = result) }
        }
    }

    fun retry() {
        search()
    }

    private fun Exception.toNetworkError(): NetworkError = when (this) {
        is IOException -> NetworkError.NO_INTERNET
        is HttpException -> NetworkError.SERVER
        else -> NetworkError.UNKNOWN
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OnlineSearchViewModel(OnlineBookRepository(RetrofitClient.api))
            }
        }
    }
}

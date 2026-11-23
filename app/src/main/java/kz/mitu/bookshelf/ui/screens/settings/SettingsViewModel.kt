package kz.mitu.bookshelf.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.mitu.bookshelf.BookShelfApp
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.data.settings.SettingsRepository

data class SettingsUiState(
    /** null — follow the system theme. */
    val darkTheme: Boolean? = null,
    val sortOption: SortOption = SortOption.RECENTLY_ADDED
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.darkTheme,
        settingsRepository.sortOption
    ) { darkTheme, sortOption ->
        SettingsUiState(darkTheme = darkTheme, sortOption = sortOption)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onDarkThemeChange(darkTheme: Boolean?) {
        viewModelScope.launch { settingsRepository.setDarkTheme(darkTheme) }
    }

    fun onSortOptionChange(sortOption: SortOption) {
        viewModelScope.launch { settingsRepository.setSortOption(sortOption) }
    }

    fun onResetSettings() {
        viewModelScope.launch { settingsRepository.resetSettings() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookShelfApp
                SettingsViewModel(app.settingsRepository)
            }
        }
    }
}

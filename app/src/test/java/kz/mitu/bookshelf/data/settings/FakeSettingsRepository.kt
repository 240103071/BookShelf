package kz.mitu.bookshelf.data.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kz.mitu.bookshelf.data.model.SortOption

class FakeSettingsRepository(
    darkTheme: Boolean? = null,
    sortOption: SortOption = SortOption.RECENTLY_ADDED
) : SettingsRepository {
    private val _darkTheme = MutableStateFlow(darkTheme)
    private val _sortOption = MutableStateFlow(sortOption)

    override val darkTheme: Flow<Boolean?> = _darkTheme
    override val sortOption: Flow<SortOption> = _sortOption

    val currentDarkTheme: Boolean? get() = _darkTheme.value
    val currentSortOption: SortOption get() = _sortOption.value

    override suspend fun setDarkTheme(darkTheme: Boolean?) {
        _darkTheme.value = darkTheme
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }
}

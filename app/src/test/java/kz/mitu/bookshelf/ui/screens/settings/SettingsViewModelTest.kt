package kz.mitu.bookshelf.ui.screens.settings

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.data.repository.FakeBookRepository
import kz.mitu.bookshelf.data.settings.FakeSettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_followsSystemTheme_andRecentlyAddedSort() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository(), FakeBookRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertNull(viewModel.uiState.value.darkTheme)
        assertEquals(SortOption.RECENTLY_ADDED, viewModel.uiState.value.sortOption)
    }

    @Test
    fun savedSettings_areShown() = runTest {
        val repository = FakeSettingsRepository(darkTheme = true, sortOption = SortOption.RATING)
        val viewModel = SettingsViewModel(repository, FakeBookRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(true, viewModel.uiState.value.darkTheme)
        assertEquals(SortOption.RATING, viewModel.uiState.value.sortOption)
    }

    @Test
    fun darkThemeChange_isSavedAndShown() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository, FakeBookRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onDarkThemeChange(false)
        assertEquals(false, repository.currentDarkTheme)
        assertEquals(false, viewModel.uiState.value.darkTheme)

        viewModel.onDarkThemeChange(true)
        assertEquals(true, viewModel.uiState.value.darkTheme)

        viewModel.onDarkThemeChange(null)
        assertNull(repository.currentDarkTheme)
        assertNull(viewModel.uiState.value.darkTheme)
    }

    @Test
    fun sortOptionChange_isSavedAndShown() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository, FakeBookRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onSortOptionChange(SortOption.TITLE)

        assertEquals(SortOption.TITLE, repository.currentSortOption)
        assertEquals(SortOption.TITLE, viewModel.uiState.value.sortOption)
    }

    @Test
    fun resetSettings_restoresDefaults() = runTest {
        val repository = FakeSettingsRepository(darkTheme = true, sortOption = SortOption.TITLE)
        val viewModel = SettingsViewModel(repository, FakeBookRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onResetSettings()

        assertNull(repository.currentDarkTheme)
        assertEquals(SortOption.RECENTLY_ADDED, repository.currentSortOption)
        assertNull(viewModel.uiState.value.darkTheme)
        assertEquals(SortOption.RECENTLY_ADDED, viewModel.uiState.value.sortOption)
    }

    @Test
    fun bookCount_updatesWhenLibraryChanges() = runTest {
        val bookRepository = FakeBookRepository()
        val viewModel = SettingsViewModel(FakeSettingsRepository(), bookRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(6, viewModel.uiState.value.bookCount)

        bookRepository.deleteBook(1)

        assertEquals(5, viewModel.uiState.value.bookCount)
    }
}

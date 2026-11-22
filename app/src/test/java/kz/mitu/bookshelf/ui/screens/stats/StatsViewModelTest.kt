package kz.mitu.bookshelf.ui.screens.stats

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.repository.FakeBookRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBookRepository
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        repository = FakeBookRepository()
        viewModel = StatsViewModel(repository)
    }

    @Test
    fun sampleBooks_giveExpectedStats() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertEquals(6, state.totalBooks)
        assertEquals(
            mapOf(
                BookStatus.WANT_TO_READ to 2,
                BookStatus.READING to 1,
                BookStatus.FINISHED to 3
            ),
            state.countByStatus
        )
        assertEquals(1524, state.pagesRead)
        assertEquals(4.5f, state.averageRating, 0.001f)
    }

    @Test
    fun stats_updateWhenRepositoryChanges() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        repository.updateStatus(2, BookStatus.FINISHED)
        repository.deleteBook(4)

        val state = viewModel.uiState.value
        assertEquals(5, state.totalBooks)
        assertEquals(0, state.countByStatus[BookStatus.READING])
        assertEquals(3, state.countByStatus[BookStatus.FINISHED])
        assertEquals(1100 + 96 + 960, state.pagesRead)
        assertEquals(13f / 3f, state.averageRating, 0.001f)
    }

    @Test
    fun noBooks_giveZeroStats() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        repository.currentBooks.map { it.id }.forEach { repository.deleteBook(it) }

        val state = viewModel.uiState.value
        assertEquals(0, state.totalBooks)
        assertEquals(BookStatus.entries.associateWith { 0 }, state.countByStatus)
        assertEquals(0, state.pagesRead)
        assertEquals(0f, state.averageRating, 0.0f)
    }
}

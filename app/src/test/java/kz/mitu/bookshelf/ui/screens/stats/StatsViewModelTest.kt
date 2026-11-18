package kz.mitu.bookshelf.ui.screens.stats

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.repository.BookRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        BookRepository.reset()
        viewModel = StatsViewModel()
    }

    @After
    fun tearDown() {
        BookRepository.reset()
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
        // FINISHED: 1100 + 328 + 96
        assertEquals(1524, state.pagesRead)
        // Rated books only (rating > 0): (5 + 4 + 5 + 4) / 4
        assertEquals(4.5f, state.averageRating, 0.001f)
    }

    @Test
    fun stats_updateWhenRepositoryChanges() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        BookRepository.updateStatus(2, BookStatus.FINISHED) // +960 pages
        BookRepository.deleteBook(4) // FINISHED, 328 pages, rating 5

        val state = viewModel.uiState.value
        assertEquals(5, state.totalBooks)
        assertEquals(0, state.countByStatus[BookStatus.READING])
        assertEquals(3, state.countByStatus[BookStatus.FINISHED])
        assertEquals(1100 + 96 + 960, state.pagesRead)
        // Remaining ratings: 5, 4, 4
        assertEquals(13f / 3f, state.averageRating, 0.001f)
    }

    @Test
    fun noBooks_giveZeroStats() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        BookRepository.books.value.map { it.id }.forEach(BookRepository::deleteBook)

        val state = viewModel.uiState.value
        assertEquals(0, state.totalBooks)
        assertEquals(BookStatus.entries.associateWith { 0 }, state.countByStatus)
        assertEquals(0, state.pagesRead)
        assertEquals(0f, state.averageRating, 0.0f)
    }
}

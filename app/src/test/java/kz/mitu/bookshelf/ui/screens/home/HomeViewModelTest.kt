package kz.mitu.bookshelf.ui.screens.home

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.data.repository.FakeBookRepository
import kz.mitu.bookshelf.data.settings.FakeSettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBookRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        repository = FakeBookRepository()
        settingsRepository = FakeSettingsRepository()
        viewModel = HomeViewModel(repository, settingsRepository)
    }

    @Test
    fun initialState_showsAllBooks_newestFirst() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertEquals(repository.currentBooks.reversed(), state.books)
        assertEquals("", state.query)
        assertNull(state.selectedStatus)
        assertEquals(SortOption.RECENTLY_ADDED, state.selectedSort)
    }

    @Test
    fun search_byTitle_isCaseInsensitive() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onQueryChange("LITTLE prince")

        val state = viewModel.uiState.value
        assertEquals("LITTLE prince", state.query)
        assertEquals(listOf(5), state.books.map { it.id })
    }

    @Test
    fun search_byAuthor_findsAllBooksOfAuthor() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onQueryChange("abai")

        assertEquals(setOf(1, 3), viewModel.uiState.value.books.map { it.id }.toSet())
    }

    @Test
    fun search_withNoMatch_returnsEmptyList() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onQueryChange("no such book")

        assertTrue(viewModel.uiState.value.books.isEmpty())
    }

    @Test
    fun statusFilter_showsOnlyBooksWithThatStatus() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onStatusFilterChange(BookStatus.WANT_TO_READ)

        val state = viewModel.uiState.value
        assertEquals(BookStatus.WANT_TO_READ, state.selectedStatus)
        assertEquals(setOf(3, 6), state.books.map { it.id }.toSet())
        assertTrue(state.books.all { it.status == BookStatus.WANT_TO_READ })
    }

    @Test
    fun statusFilter_null_showsAllBooks() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onStatusFilterChange(BookStatus.READING)
        viewModel.onStatusFilterChange(null)

        assertEquals(repository.currentBooks.size, viewModel.uiState.value.books.size)
    }

    @Test
    fun searchAndStatusFilter_areCombined() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onQueryChange("abai")
        viewModel.onStatusFilterChange(BookStatus.FINISHED)

        assertEquals(listOf(1), viewModel.uiState.value.books.map { it.id })
    }

    @Test
    fun repositoryChanges_areReflected() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        viewModel.onStatusFilterChange(BookStatus.READING)

        repository.updateStatus(6, BookStatus.READING)

        assertEquals(setOf(2, 6), viewModel.uiState.value.books.map { it.id }.toSet())
    }

    private fun ids() = viewModel.uiState.value.books.map { it.id }

    private fun newBook(title: String, rating: Int = 0) = Book(
        id = 0, title = title, author = "Author", genre = "Genre", pages = 100,
        rating = rating, status = BookStatus.WANT_TO_READ, description = ""
    )

    @Test
    fun sortByTitle_isAlphabetical() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onSortChange(SortOption.TITLE)

        assertEquals(SortOption.TITLE, viewModel.uiState.value.selectedSort)
        assertEquals(listOf(4, 3, 6, 5, 2, 1), ids())
    }

    @Test
    fun sortByTitle_ignoresCase() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        repository.addBook(newBook(title = "apple"))

        viewModel.onSortChange(SortOption.TITLE)

        assertEquals(listOf(4, 7, 3, 6, 5, 2, 1), ids())
    }

    @Test
    fun sortByRating_highestFirst_tiesByTitle() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onSortChange(SortOption.RATING)

        assertEquals(listOf(4, 1, 5, 2, 3, 6), ids())
        val ratings = viewModel.uiState.value.books.map { it.rating }
        assertEquals(ratings.sortedDescending(), ratings)
    }

    @Test
    fun sortByRecentlyAdded_newestFirst() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        viewModel.onSortChange(SortOption.TITLE)

        viewModel.onSortChange(SortOption.RECENTLY_ADDED)
        repository.addBook(newBook(title = "Newest"))

        assertEquals(SortOption.RECENTLY_ADDED, viewModel.uiState.value.selectedSort)
        assertEquals(listOf(7, 6, 5, 4, 3, 2, 1), ids())
    }

    @Test
    fun sorting_isAppliedAfterFiltering() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onStatusFilterChange(BookStatus.FINISHED)
        viewModel.onSortChange(SortOption.TITLE)

        assertEquals(listOf(4, 5, 1), ids())
    }

    @Test
    fun genres_areDistinctAndSorted() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(
            listOf("Dystopia", "Fable", "Historical novel", "Philosophy", "Psychological novel"),
            viewModel.uiState.value.genres
        )
    }

    @Test
    fun genres_includeAllBooks_notOnlyFiltered() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onQueryChange("1984")
        viewModel.onStatusFilterChange(BookStatus.FINISHED)

        assertEquals(1, viewModel.uiState.value.books.size)
        assertEquals(5, viewModel.uiState.value.genres.size)
    }

    @Test
    fun genres_updateWhenRepositoryChanges_andSkipBlank() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        repository.addBook(newBook(title = "A").copy(genre = "Adventure"))
        repository.addBook(newBook(title = "B").copy(genre = "  "))
        repository.deleteBook(5)

        assertEquals(
            listOf("Adventure", "Dystopia", "Historical novel", "Philosophy", "Psychological novel"),
            viewModel.uiState.value.genres
        )
    }

    @Test
    fun genreFilter_showsOnlyBooksOfThatGenre() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onGenreChange("Historical novel")

        assertEquals("Historical novel", viewModel.uiState.value.selectedGenre)
        assertEquals(setOf(1, 2), ids().toSet())
    }

    @Test
    fun genreFilter_null_showsAllBooks() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onGenreChange("Fable")
        viewModel.onGenreChange(null)

        assertEquals(6, ids().size)
    }

    @Test
    fun genreFilter_isCombinedWithSearchAndStatus() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onGenreChange("Historical novel")
        viewModel.onStatusFilterChange(BookStatus.READING)
        assertEquals(listOf(2), ids())

        viewModel.onStatusFilterChange(null)
        viewModel.onQueryChange("abai")
        assertEquals(listOf(1), ids())
    }

    @Test
    fun clearFilters_resetsQueryStatusAndGenre_butKeepsSort() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        viewModel.onQueryChange("abai")
        viewModel.onStatusFilterChange(BookStatus.FINISHED)
        viewModel.onGenreChange("Historical novel")
        viewModel.onSortChange(SortOption.TITLE)

        viewModel.clearFilters()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertNull(state.selectedStatus)
        assertNull(state.selectedGenre)
        assertEquals(SortOption.TITLE, state.selectedSort)
        assertEquals(6, state.books.size)
    }

    @Test
    fun sortChange_isSavedToSettings() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onSortChange(SortOption.RATING)

        assertEquals(SortOption.RATING, settingsRepository.currentSortOption)
    }

    @Test
    fun savedSort_isUsedOnStart() = runTest {
        settingsRepository = FakeSettingsRepository(sortOption = SortOption.TITLE)
        viewModel = HomeViewModel(repository, settingsRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(SortOption.TITLE, viewModel.uiState.value.selectedSort)
        assertEquals(listOf(4, 3, 6, 5, 2, 1), ids())
    }
}

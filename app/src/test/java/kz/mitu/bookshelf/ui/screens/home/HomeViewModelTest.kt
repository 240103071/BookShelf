package kz.mitu.bookshelf.ui.screens.home

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.data.repository.BookRepository
import org.junit.After
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

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        BookRepository.reset()
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        BookRepository.reset()
    }

    @Test
    fun initialState_showsAllBooks_newestFirst() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertEquals(BookRepository.books.value.reversed(), state.books)
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

        // "The Path of Abai" (title) and "Book of Words" by Abai Kunanbayev (author)
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

        assertEquals(BookRepository.books.value.size, viewModel.uiState.value.books.size)
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

        BookRepository.updateStatus(6, BookStatus.READING)

        assertEquals(setOf(2, 6), viewModel.uiState.value.books.map { it.id }.toSet())
    }

    // --- Sorting ---

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
        // 1984, Book of Words, Crime and Punishment, The Little Prince, The Nomads, The Path of Abai
        assertEquals(listOf(4, 3, 6, 5, 2, 1), ids())
    }

    @Test
    fun sortByTitle_ignoresCase() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        BookRepository.addBook(newBook(title = "apple")) // id 7

        viewModel.onSortChange(SortOption.TITLE)

        assertEquals(listOf(4, 7, 3, 6, 5, 2, 1), ids())
    }

    @Test
    fun sortByRating_highestFirst_tiesByTitle() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onSortChange(SortOption.RATING)

        // 5: 1984, The Path of Abai; 4: The Little Prince, The Nomads; 0: Book of Words, Crime and Punishment
        assertEquals(listOf(4, 1, 5, 2, 3, 6), ids())
        val ratings = viewModel.uiState.value.books.map { it.rating }
        assertEquals(ratings.sortedDescending(), ratings)
    }

    @Test
    fun sortByRecentlyAdded_newestFirst() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        viewModel.onSortChange(SortOption.TITLE)

        viewModel.onSortChange(SortOption.RECENTLY_ADDED)
        BookRepository.addBook(newBook(title = "Newest")) // id 7

        assertEquals(SortOption.RECENTLY_ADDED, viewModel.uiState.value.selectedSort)
        assertEquals(listOf(7, 6, 5, 4, 3, 2, 1), ids())
    }

    @Test
    fun sorting_isAppliedAfterFiltering() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onStatusFilterChange(BookStatus.FINISHED)
        viewModel.onSortChange(SortOption.TITLE)

        // FINISHED only: 1984, The Little Prince, The Path of Abai
        assertEquals(listOf(4, 5, 1), ids())
    }
}

package kz.mitu.bookshelf.ui.screens.online

import java.io.IOException
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.remote.FakeOpenLibraryApi
import kz.mitu.bookshelf.data.remote.dto.BookDocDto
import kz.mitu.bookshelf.data.remote.dto.SearchResponseDto
import kz.mitu.bookshelf.data.repository.FakeBookRepository
import kz.mitu.bookshelf.data.repository.OnlineBookRepository
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class OnlineSearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var api: FakeOpenLibraryApi
    private lateinit var bookRepository: FakeBookRepository

    private val crimeAndPunishment = BookDocDto(
        key = "/works/OL166894W",
        title = "Crime and Punishment",
        authorNames = listOf("Fyodor Dostoevsky"),
        firstPublishYear = 1866,
        coverId = 8479576L,
        pages = 551,
        subjects = listOf("Fiction")
    )

    @Before
    fun setUp() {
        api = FakeOpenLibraryApi()
        bookRepository = FakeBookRepository()
    }

    private fun createViewModel() = OnlineSearchViewModel(
        onlineRepository = OnlineBookRepository(api),
        bookRepository = bookRepository
    )

    @Test
    fun init_searchesDefaultQuery_andShowsSuccess() {
        api.docs = listOf(crimeAndPunishment)

        val viewModel = createViewModel()

        assertEquals(DEFAULT_ONLINE_QUERY, api.lastQuery)
        val result = viewModel.uiState.value.result as SearchResult.Success
        val book = result.books.single()
        assertEquals("Crime and Punishment", book.title)
        assertEquals("Fyodor Dostoevsky", book.author)
        assertEquals(1866, book.year)
        assertEquals("https://covers.openlibrary.org/b/id/8479576-M.jpg", book.coverUrl)
    }

    @Test
    fun emptyResponse_showsEmpty() {
        api.docs = emptyList()

        val viewModel = createViewModel()

        assertEquals(SearchResult.Empty, viewModel.uiState.value.result)
    }

    @Test
    fun docsWithoutTitle_areSkipped() {
        api.docs = listOf(crimeAndPunishment, crimeAndPunishment.copy(key = "/works/X", title = null))

        val viewModel = createViewModel()

        val result = viewModel.uiState.value.result as SearchResult.Success
        assertEquals(1, result.books.size)
    }

    @Test
    fun bookWithoutCover_hasNullCoverUrl() {
        api.docs = listOf(crimeAndPunishment.copy(coverId = null))

        val viewModel = createViewModel()

        val result = viewModel.uiState.value.result as SearchResult.Success
        assertNull(result.books.single().coverUrl)
    }

    @Test
    fun noInternet_showsNoInternetError() {
        api.error = IOException("offline")

        val viewModel = createViewModel()

        assertEquals(SearchResult.Error(NetworkError.NO_INTERNET), viewModel.uiState.value.result)
    }

    @Test
    fun serverError_showsServerError() {
        api.error = HttpException(Response.error<SearchResponseDto>(500, "".toResponseBody(null)))

        val viewModel = createViewModel()

        assertEquals(SearchResult.Error(NetworkError.SERVER), viewModel.uiState.value.result)
    }

    @Test
    fun retry_afterError_loadsBooks() {
        api.error = IOException("offline")
        val viewModel = createViewModel()

        api.error = null
        api.docs = listOf(crimeAndPunishment)
        viewModel.retry()

        assertTrue(viewModel.uiState.value.result is SearchResult.Success)
        assertEquals(2, api.callCount)
    }

    @Test
    fun search_usesNewQuery() {
        val viewModel = createViewModel()

        viewModel.onQueryChange("  Abai  ")
        viewModel.search()

        assertEquals("Abai", api.lastQuery)
    }

    @Test
    fun blankQuery_showsEmpty_withoutCallingApi() {
        val viewModel = createViewModel()
        val callsBefore = api.callCount

        viewModel.onQueryChange("   ")
        viewModel.search()

        assertEquals(SearchResult.Empty, viewModel.uiState.value.result)
        assertEquals(callsBefore, api.callCount)
    }

    @Test
    fun addToLibrary_savesBookOnce_andMarksItAdded() {
        api.docs = listOf(crimeAndPunishment)
        val viewModel = createViewModel()
        val onlineBook = (viewModel.uiState.value.result as SearchResult.Success).books.single()
        val sizeBefore = bookRepository.currentBooks.size

        viewModel.addToLibrary(onlineBook)
        viewModel.addToLibrary(onlineBook)

        assertEquals(sizeBefore + 1, bookRepository.currentBooks.size)
        assertTrue(onlineBook.key in viewModel.uiState.value.addedKeys)
        val saved = bookRepository.currentBooks.last()
        assertEquals("Crime and Punishment", saved.title)
        assertEquals("Fyodor Dostoevsky", saved.author)
        assertEquals(551, saved.pages)
        assertEquals(BookStatus.WANT_TO_READ, saved.status)
    }
}

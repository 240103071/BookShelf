package kz.mitu.bookshelf.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.repository.FakeBookRepository
import kz.mitu.bookshelf.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditBookViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBookRepository
    private lateinit var viewModel: AddEditBookViewModel

    private val state get() = viewModel.uiState.value

    @Before
    fun setUp() {
        repository = FakeBookRepository()
        viewModel = AddEditBookViewModel(SavedStateHandle(), repository)
    }

    private fun fillValidForm() {
        viewModel.onTitleChange("Dune")
        viewModel.onAuthorChange("Frank Herbert")
        viewModel.onGenreChange("Science fiction")
        viewModel.onPagesChange("412")
        viewModel.onRatingChange(4)
        viewModel.onStatusChange(BookStatus.READING)
        viewModel.onDescriptionChange("Desert planet")
    }

    @Test
    fun newViewModel_isInAddMode() {
        assertFalse(state.isEditMode)
        assertEquals("", state.title)
    }

    @Test
    fun save_withEmptyForm_setsAllErrors_andDoesNotSave() {
        val sizeBefore = repository.currentBooks.size

        assertFalse(viewModel.save())

        assertTrue(state.titleError)
        assertTrue(state.authorError)
        assertTrue(state.pagesError)
        assertEquals(sizeBefore, repository.currentBooks.size)
    }

    @Test
    fun save_withBlankTitle_setsTitleErrorOnly() {
        fillValidForm()
        viewModel.onTitleChange("   ")

        assertFalse(viewModel.save())

        assertTrue(state.titleError)
        assertFalse(state.authorError)
        assertFalse(state.pagesError)
    }

    @Test
    fun save_withBlankAuthor_setsAuthorErrorOnly() {
        fillValidForm()
        viewModel.onAuthorChange("")

        assertFalse(viewModel.save())

        assertFalse(state.titleError)
        assertTrue(state.authorError)
        assertFalse(state.pagesError)
    }

    @Test
    fun save_withInvalidPages_setsPagesError() {
        fillValidForm()
        for (pages in listOf("", "abc", "0", "-5", "12.5")) {
            viewModel.onPagesChange(pages)

            assertFalse("pages = \"$pages\" should be invalid", viewModel.save())
            assertTrue("pages = \"$pages\" should set pagesError", state.pagesError)
        }
    }

    @Test
    fun changingField_clearsItsError() {
        viewModel.save()

        viewModel.onTitleChange("A")
        viewModel.onAuthorChange("B")
        viewModel.onPagesChange("1")

        assertFalse(state.titleError)
        assertFalse(state.authorError)
        assertFalse(state.pagesError)
    }

    @Test
    fun save_withValidForm_addsBook() {
        val sizeBefore = repository.currentBooks.size
        val maxIdBefore = repository.currentBooks.maxOf { it.id }
        fillValidForm()

        assertTrue(viewModel.save())

        val books = repository.currentBooks
        assertEquals(sizeBefore + 1, books.size)
        val added = books.last()
        assertEquals(maxIdBefore + 1, added.id)
        assertEquals("Dune", added.title)
        assertEquals("Frank Herbert", added.author)
        assertEquals("Science fiction", added.genre)
        assertEquals(412, added.pages)
        assertEquals(4, added.rating)
        assertEquals(BookStatus.READING, added.status)
        assertEquals("Desert planet", added.description)
    }

    @Test
    fun editMode_loadsBook_andSaveUpdatesIt() {
        viewModel = AddEditBookViewModel(SavedStateHandle(mapOf(Screen.ARG_BOOK_ID to 4)), repository)
        val sizeBefore = repository.currentBooks.size

        assertTrue(state.isEditMode)
        assertEquals("1984", state.title)
        assertEquals("328", state.pages)

        viewModel.onTitleChange("Nineteen Eighty-Four")
        assertTrue(viewModel.save())

        val books = repository.currentBooks
        assertEquals(sizeBefore, books.size)
        assertEquals("Nineteen Eighty-Four", books.first { it.id == 4 }.title)
    }
}

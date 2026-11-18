package kz.mitu.bookshelf.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import kz.mitu.bookshelf.MainDispatcherRule
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.data.repository.BookRepository
import kz.mitu.bookshelf.navigation.Screen
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditBookViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddEditBookViewModel

    private val state get() = viewModel.uiState.value

    @Before
    fun setUp() {
        BookRepository.reset()
        viewModel = AddEditBookViewModel(SavedStateHandle())
    }

    @After
    fun tearDown() {
        BookRepository.reset()
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
        val sizeBefore = BookRepository.books.value.size

        assertFalse(viewModel.save())

        assertTrue(state.titleError)
        assertTrue(state.authorError)
        assertTrue(state.pagesError)
        assertEquals(sizeBefore, BookRepository.books.value.size)
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
        val sizeBefore = BookRepository.books.value.size
        val maxIdBefore = BookRepository.books.value.maxOf { it.id }
        fillValidForm()

        assertTrue(viewModel.save())

        val books = BookRepository.books.value
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
        viewModel = AddEditBookViewModel(SavedStateHandle(mapOf(Screen.ARG_BOOK_ID to 4)))
        val sizeBefore = BookRepository.books.value.size

        assertTrue(state.isEditMode)
        assertEquals("1984", state.title)
        assertEquals("328", state.pages)

        viewModel.onTitleChange("Nineteen Eighty-Four")
        assertTrue(viewModel.save())

        val books = BookRepository.books.value
        assertEquals(sizeBefore, books.size)
        assertEquals("Nineteen Eighty-Four", books.first { it.id == 4 }.title)
    }
}

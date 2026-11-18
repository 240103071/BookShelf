package kz.mitu.bookshelf.data.repository

import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BookRepositoryTest {

    private val books get() = BookRepository.books.value

    private fun findBook(id: Int): Book? = books.find { it.id == id }

    private fun newBook(id: Int = 0) = Book(
        id = id,
        title = "Test title",
        author = "Test author",
        genre = "Test genre",
        pages = 123,
        rating = 3,
        status = BookStatus.WANT_TO_READ,
        description = "Test description"
    )

    @Before
    fun setUp() {
        BookRepository.reset()
    }

    @After
    fun tearDown() {
        BookRepository.reset()
    }

    @Test
    fun addBook_generatesNewUniqueId() {
        val sizeBefore = books.size
        val maxIdBefore = books.maxOf { it.id }

        BookRepository.addBook(newBook(id = 1)) // passed id must be ignored

        assertEquals(sizeBefore + 1, books.size)
        val added = books.last()
        assertEquals(maxIdBefore + 1, added.id)
        assertEquals("Test title", added.title)
        assertEquals(books.size, books.map { it.id }.toSet().size)
    }

    @Test
    fun addBook_twice_givesDifferentIds() {
        BookRepository.addBook(newBook())
        BookRepository.addBook(newBook())

        val lastTwo = books.takeLast(2)
        assertEquals(lastTwo[0].id + 1, lastTwo[1].id)
    }

    @Test
    fun updateBook_changesOnlyTheMatchingBook() {
        val original = findBook(2)!!
        val others = books.filter { it.id != 2 }

        BookRepository.updateBook(original.copy(title = "Updated", rating = 1))

        val updated = findBook(2)!!
        assertEquals("Updated", updated.title)
        assertEquals(1, updated.rating)
        assertEquals(original.author, updated.author)
        assertEquals(others, books.filter { it.id != 2 })
    }

    @Test
    fun deleteBook_removesIt() {
        val sizeBefore = books.size
        assertNotNull(findBook(3))

        BookRepository.deleteBook(3)

        assertNull(findBook(3))
        assertEquals(sizeBefore - 1, books.size)
    }

    @Test
    fun deleteBook_unknownId_changesNothing() {
        val before = books

        BookRepository.deleteBook(999)

        assertEquals(before, books)
    }

    @Test
    fun updateStatus_changesStatusOfTheRightBook() {
        val original = findBook(6)!!
        assertFalse(original.status == BookStatus.READING)
        val others = books.filter { it.id != 6 }

        BookRepository.updateStatus(6, BookStatus.READING)

        assertEquals(original.copy(status = BookStatus.READING), findBook(6))
        assertEquals(others, books.filter { it.id != 6 })
    }

    @Test
    fun reset_restoresSampleBooks() {
        val initial = books
        BookRepository.deleteBook(1)
        BookRepository.addBook(newBook())

        BookRepository.reset()

        assertEquals(initial, books)
    }
}

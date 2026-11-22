package kz.mitu.bookshelf.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kz.mitu.bookshelf.data.local.sampleBooks
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus

class FakeBookRepository(initialBooks: List<Book> = sampleBooks) : BookRepository {
    private val _books = MutableStateFlow(initialBooks)
    override val books: Flow<List<Book>> = _books

    val currentBooks: List<Book> get() = _books.value

    override fun getBook(id: Int): Flow<Book?> =
        _books.map { list -> list.find { it.id == id } }

    override suspend fun addBook(book: Book): Int {
        val nextId = (_books.value.maxOfOrNull { it.id } ?: 0) + 1
        _books.update { it + book.copy(id = nextId) }
        return nextId
    }

    override suspend fun updateBook(book: Book) {
        _books.update { list -> list.map { if (it.id == book.id) book else it } }
    }

    override suspend fun deleteBook(id: Int) {
        _books.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun updateStatus(id: Int, status: BookStatus) {
        _books.update { list -> list.map { if (it.id == id) it.copy(status = status) else it } }
    }
}

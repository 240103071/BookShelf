package kz.mitu.bookshelf.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kz.mitu.bookshelf.data.local.sampleBooks
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus

object BookRepository {
    private val _books = MutableStateFlow(sampleBooks)
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    fun getBook(id: Int): Flow<Book?> =
        books.map { list -> list.find { it.id == id } }

    fun addBook(book: Book) {
        _books.update { list ->
            val nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list + book.copy(id = nextId)
        }
    }

    fun updateBook(book: Book) {
        _books.update { list ->
            list.map { if (it.id == book.id) book else it }
        }
    }

    fun deleteBook(id: Int) {
        _books.update { list -> list.filterNot { it.id == id } }
    }

    fun updateStatus(id: Int, status: BookStatus) {
        _books.update { list ->
            list.map { if (it.id == id) it.copy(status = status) else it }
        }
    }

    internal fun reset() {
        _books.value = sampleBooks
    }
}

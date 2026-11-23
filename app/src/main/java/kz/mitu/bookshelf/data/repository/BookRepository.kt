package kz.mitu.bookshelf.data.repository

import kotlinx.coroutines.flow.Flow
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus

interface BookRepository {
    val books: Flow<List<Book>>

    val bookCount: Flow<Int>

    fun getBook(id: Int): Flow<Book?>

    suspend fun addBook(book: Book): Int

    suspend fun updateBook(book: Book)

    suspend fun deleteBook(id: Int)

    suspend fun updateStatus(id: Int, status: BookStatus)
}

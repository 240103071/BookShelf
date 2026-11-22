package kz.mitu.bookshelf.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.mitu.bookshelf.data.local.BookDao
import kz.mitu.bookshelf.data.local.toBook
import kz.mitu.bookshelf.data.local.toEntity
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus

class RoomBookRepository(private val bookDao: BookDao) : BookRepository {
    override val books: Flow<List<Book>> =
        bookDao.getAll().map { entities -> entities.map { it.toBook() } }

    override fun getBook(id: Int): Flow<Book?> =
        bookDao.getById(id).map { it?.toBook() }

    override suspend fun addBook(book: Book): Int =
        bookDao.insert(book.copy(id = 0).toEntity()).toInt()

    override suspend fun updateBook(book: Book) {
        bookDao.update(book.toEntity())
    }

    override suspend fun deleteBook(id: Int) {
        bookDao.deleteById(id)
    }

    override suspend fun updateStatus(id: Int, status: BookStatus) {
        bookDao.updateStatus(id, status.name)
    }
}

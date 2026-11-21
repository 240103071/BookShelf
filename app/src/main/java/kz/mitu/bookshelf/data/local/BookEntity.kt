package kz.mitu.bookshelf.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val genre: String,
    val pages: Int,
    val rating: Int,
    val status: String,
    val description: String
)

fun BookEntity.toBook(): Book = Book(
    id = id,
    title = title,
    author = author,
    genre = genre,
    pages = pages,
    rating = rating,
    status = BookStatus.entries.firstOrNull { it.name == status } ?: BookStatus.WANT_TO_READ,
    description = description
)

fun Book.toEntity(): BookEntity = BookEntity(
    id = id,
    title = title,
    author = author,
    genre = genre,
    pages = pages,
    rating = rating,
    status = status.name,
    description = description
)

package kz.mitu.bookshelf.data.model

data class OnlineBook(
    val key: String,
    val title: String,
    val author: String,
    val year: Int?,
    val pages: Int?,
    val genre: String,
    val coverUrl: String?
)

fun OnlineBook.toBook(): Book = Book(
    id = 0,
    title = title,
    author = author.ifBlank { "Unknown author" },
    genre = genre,
    pages = pages ?: 0,
    rating = 0,
    status = BookStatus.WANT_TO_READ,
    description = year?.let { "First published in $it." }.orEmpty()
)

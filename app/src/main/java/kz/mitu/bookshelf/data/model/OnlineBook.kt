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

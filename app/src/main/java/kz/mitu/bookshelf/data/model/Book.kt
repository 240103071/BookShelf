package kz.mitu.bookshelf.data.model

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String,
    val pages: Int,
    val rating: Int,
    val status: BookStatus,
    val description: String
)

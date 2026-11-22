package kz.mitu.bookshelf.data.repository

import kz.mitu.bookshelf.data.model.OnlineBook
import kz.mitu.bookshelf.data.remote.OpenLibraryApi
import kz.mitu.bookshelf.data.remote.dto.BookDocDto

class OnlineBookRepository(private val api: OpenLibraryApi) {
    suspend fun searchBooks(query: String): List<OnlineBook> =
        api.searchBooks(query).docs.orEmpty().mapNotNull { it.toOnlineBook() }
}

private fun BookDocDto.toOnlineBook(): OnlineBook? {
    val key = key ?: return null
    val title = title?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return OnlineBook(
        key = key,
        title = title,
        author = authorNames.orEmpty().take(2).joinToString(", "),
        year = firstPublishYear,
        pages = pages?.takeIf { it > 0 },
        genre = subjects?.firstOrNull()?.trim().orEmpty(),
        coverUrl = coverId?.let { OpenLibraryApi.coverUrl(it) }
    )
}

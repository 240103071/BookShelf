package kz.mitu.bookshelf.data.remote

import kz.mitu.bookshelf.data.remote.dto.BookDocDto
import kz.mitu.bookshelf.data.remote.dto.SearchResponseDto

class FakeOpenLibraryApi : OpenLibraryApi {
    var docs: List<BookDocDto> = emptyList()
    var error: Exception? = null
    var callCount = 0
        private set
    var lastQuery: String? = null
        private set

    override suspend fun searchBooks(query: String, limit: Int, fields: String): SearchResponseDto {
        callCount++
        lastQuery = query
        error?.let { throw it }
        return SearchResponseDto(numFound = docs.size, docs = docs)
    }
}

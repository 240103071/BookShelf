package kz.mitu.bookshelf.data.remote

import kz.mitu.bookshelf.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = SEARCH_FIELDS
    ): SearchResponseDto

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
        const val SEARCH_FIELDS =
            "key,title,author_name,first_publish_year,cover_i,number_of_pages_median,subject"

        fun coverUrl(coverId: Long): String = "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
    }
}

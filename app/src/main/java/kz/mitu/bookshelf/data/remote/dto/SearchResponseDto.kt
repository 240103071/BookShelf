package kz.mitu.bookshelf.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SearchResponseDto(
    @SerializedName("numFound") val numFound: Int?,
    @SerializedName("docs") val docs: List<BookDocDto>?
)

package kz.mitu.bookshelf.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BookDocDto(
    @SerializedName("key") val key: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("author_name") val authorNames: List<String>?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    @SerializedName("cover_i") val coverId: Long?,
    @SerializedName("number_of_pages_median") val pages: Int?,
    @SerializedName("subject") val subjects: List<String>?
)

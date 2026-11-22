package kz.mitu.bookshelf.data.model

import androidx.annotation.StringRes
import kz.mitu.bookshelf.R

enum class SortOption(@StringRes val labelRes: Int) {
    TITLE(R.string.sort_title),

    RATING(R.string.sort_rating),

    RECENTLY_ADDED(R.string.sort_recent)
}

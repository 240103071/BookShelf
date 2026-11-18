package kz.mitu.bookshelf.data.model

import androidx.annotation.StringRes
import kz.mitu.bookshelf.R

enum class BookStatus(@StringRes val labelRes: Int) {
    WANT_TO_READ(R.string.status_want_to_read),
    READING(R.string.status_reading),
    FINISHED(R.string.status_finished)
}

package kz.mitu.bookshelf.data.model

enum class SortOption {
    /** Alphabetically by title (A–Z, case-insensitive). */
    TITLE,

    /** Highest rating first. */
    RATING,

    /** Newest books first (the repository gives new books the highest id). */
    RECENTLY_ADDED
}

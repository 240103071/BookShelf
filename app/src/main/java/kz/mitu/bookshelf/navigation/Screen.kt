package kz.mitu.bookshelf.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Detail : Screen("detail/{$ARG_BOOK_ID}") {
        fun createRoute(bookId: Int) = "detail/$bookId"
    }

    data object Add : Screen("add")

    data object Edit : Screen("edit/{$ARG_BOOK_ID}") {
        fun createRoute(bookId: Int) = "edit/$bookId"
    }

    data object Stats : Screen("stats")

    companion object {
        const val ARG_BOOK_ID = "bookId"
    }
}

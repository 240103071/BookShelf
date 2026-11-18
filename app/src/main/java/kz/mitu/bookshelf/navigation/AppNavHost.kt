package kz.mitu.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kz.mitu.bookshelf.ui.screens.add.AddEditBookScreen
import kz.mitu.bookshelf.ui.screens.add.AddEditBookViewModel
import kz.mitu.bookshelf.ui.screens.detail.BookDetailScreen
import kz.mitu.bookshelf.ui.screens.detail.DetailViewModel
import kz.mitu.bookshelf.ui.screens.home.HomeScreen
import kz.mitu.bookshelf.ui.screens.home.HomeViewModel
import kz.mitu.bookshelf.ui.screens.stats.StatsScreen
import kz.mitu.bookshelf.ui.screens.stats.StatsViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                books = uiState.books,
                query = uiState.query,
                selectedStatus = uiState.selectedStatus,
                selectedSort = uiState.selectedSort,
                onQueryChange = viewModel::onQueryChange,
                onStatusSelected = viewModel::onStatusFilterChange,
                onSortChange = viewModel::onSortChange,
                onBookClick = { bookId -> navController.navigate(Screen.Detail.createRoute(bookId)) },
                onAddClick = { navController.navigate(Screen.Add.route) },
                onStatsClick = { navController.navigate(Screen.Stats.route) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.ARG_BOOK_ID) { type = NavType.IntType })
        ) {
            // DetailViewModel reads bookId from SavedStateHandle by itself
            val viewModel: DetailViewModel = viewModel()
            val book by viewModel.book.collectAsStateWithLifecycle()

            BookDetailScreen(
                book = book,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    book?.let { navController.navigate(Screen.Edit.createRoute(it.id)) }
                },
                onStatusChange = viewModel::changeStatus,
                onRatingChange = viewModel::updateRating,
                onDeleteClick = {
                    viewModel.deleteBook()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Add.route) {
            AddEditDestination(onDone = { navController.popBackStack() })
        }

        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument(Screen.ARG_BOOK_ID) { type = NavType.IntType })
        ) {
            AddEditDestination(onDone = { navController.popBackStack() })
        }

        composable(Screen.Stats.route) {
            val viewModel: StatsViewModel = viewModel()
            val stats by viewModel.uiState.collectAsStateWithLifecycle()

            StatsScreen(
                totalBooks = stats.totalBooks,
                countByStatus = stats.countByStatus,
                pagesRead = stats.pagesRead,
                averageRating = stats.averageRating,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

/** Add and Edit use the same form; edit mode comes from the "bookId" argument. */
@Composable
private fun AddEditDestination(onDone: () -> Unit) {
    val viewModel: AddEditBookViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditBookScreen(
        isEditMode = state.isEditMode,
        title = state.title,
        author = state.author,
        genre = state.genre,
        pages = state.pages,
        rating = state.rating,
        status = state.status,
        description = state.description,
        titleError = state.titleError,
        authorError = state.authorError,
        pagesError = state.pagesError,
        onTitleChange = viewModel::onTitleChange,
        onAuthorChange = viewModel::onAuthorChange,
        onGenreChange = viewModel::onGenreChange,
        onPagesChange = viewModel::onPagesChange,
        onRatingChange = viewModel::onRatingChange,
        onStatusChange = viewModel::onStatusChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSaveClick = { if (viewModel.save()) onDone() },
        onBackClick = onDone
    )
}

package kz.mitu.bookshelf.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kz.mitu.bookshelf.R

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
            PlaceholderScreen(title = stringResource(R.string.my_books)) {
                Button(onClick = { navController.navigate(Screen.Detail.createRoute(1)) }) {
                    Text(stringResource(R.string.open_book_1))
                }
                Button(onClick = { navController.navigate(Screen.Add.route) }) {
                    Text(stringResource(R.string.add_book))
                }
            }
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.ARG_BOOK_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt(Screen.ARG_BOOK_ID) ?: -1
            PlaceholderScreen(title = stringResource(R.string.book_details)) {
                Text(stringResource(R.string.placeholder_book_id, bookId))
            }
        }

        composable(Screen.Add.route) {
            PlaceholderScreen(title = stringResource(R.string.add_book))
        }

        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument(Screen.ARG_BOOK_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt(Screen.ARG_BOOK_ID) ?: -1
            PlaceholderScreen(title = stringResource(R.string.edit_book)) {
                Text(stringResource(R.string.placeholder_book_id, bookId))
            }
        }

        composable(Screen.Stats.route) {
            PlaceholderScreen(title = stringResource(R.string.statistics))
        }
    }
}

/** Temporary screen used until the real screens are implemented. */
@Composable
private fun PlaceholderScreen(
    title: String,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.placeholder_screen, title),
            style = MaterialTheme.typography.titleLarge
        )
        content()
    }
}

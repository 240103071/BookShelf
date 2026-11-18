package kz.mitu.bookshelf.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.ui.components.BookCard
import kz.mitu.bookshelf.ui.components.BookShelfTopBar
import kz.mitu.bookshelf.ui.components.EmptyState
import kz.mitu.bookshelf.ui.components.FilterChipRow
import kz.mitu.bookshelf.ui.components.SearchField
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

/**
 * Start screen: search, status filter, list of books, "+" button
 * and a "Statistics" button in the top bar.
 * Stateless: data comes in as parameters, actions go out as callbacks.
 * The ViewModel is connected in the navigation graph.
 */
@Composable
fun HomeScreen(
    books: List<Book>,
    query: String,
    selectedStatus: BookStatus?,
    onQueryChange: (String) -> Unit,
    onStatusSelected: (BookStatus?) -> Unit,
    onBookClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onStatsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BookShelfTopBar(
                title = stringResource(R.string.my_books),
                actions = {
                    TextButton(onClick = onStatsClick) {
                        Text(text = stringResource(R.string.statistics))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_book)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchField(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            FilterChipRow(
                options = BookStatus.entries,
                selected = selectedStatus,
                onSelected = onStatusSelected,
                label = { status -> stringResource(status.labelRes) },
                allLabel = stringResource(R.string.filter_all)
            )

            if (books.isEmpty()) {
                val isFiltering = query.isNotBlank() || selectedStatus != null
                EmptyState(
                    title = stringResource(
                        if (isFiltering) R.string.empty_search_title else R.string.empty_books_title
                    ),
                    message = stringResource(
                        if (isFiltering) R.string.empty_search_message else R.string.empty_books_message
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books, key = { book -> book.id }) { book ->
                        BookCard(
                            title = book.title,
                            author = book.author,
                            statusLabel = stringResource(book.status.labelRes),
                            rating = book.rating,
                            onClick = { onBookClick(book.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    BookShelfTheme {
        HomeScreen(
            books = listOf(
                Book(
                    id = 1, title = "The Path of Abai", author = "Mukhtar Auezov",
                    genre = "Historical novel", pages = 1100, rating = 5,
                    status = BookStatus.FINISHED, description = ""
                ),
                Book(
                    id = 2, title = "The Nomads", author = "Ilyas Yesenberlin",
                    genre = "Historical novel", pages = 960, rating = 4,
                    status = BookStatus.READING, description = ""
                )
            ),
            query = "",
            selectedStatus = null,
            onQueryChange = {},
            onStatusSelected = {},
            onBookClick = {},
            onAddClick = {},
            onStatsClick = {}
        )
    }
}

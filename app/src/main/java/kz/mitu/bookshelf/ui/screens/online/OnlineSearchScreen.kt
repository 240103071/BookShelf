package kz.mitu.bookshelf.ui.screens.online

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.data.model.OnlineBook
import kz.mitu.bookshelf.ui.components.BookShelfTopBar
import kz.mitu.bookshelf.ui.components.EmptyState
import kz.mitu.bookshelf.ui.components.OnlineBookCard
import kz.mitu.bookshelf.ui.components.SearchField
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun OnlineSearchScreen(
    query: String,
    result: SearchResult,
    addedKeys: Set<String>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onAddClick: (OnlineBook) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val startSearch = {
        keyboard?.hide()
        onSearch()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BookShelfTopBar(
                title = stringResource(R.string.online_search_title),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    placeholder = stringResource(R.string.online_search_hint),
                    onSearch = startSearch,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = startSearch) {
                    Text(text = stringResource(R.string.search))
                }
            }
            Text(
                text = stringResource(R.string.data_source),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            when (result) {
                SearchResult.Loading -> LoadingState()

                is SearchResult.Success -> BookResults(
                    books = result.books,
                    addedKeys = addedKeys,
                    onAddClick = onAddClick
                )

                SearchResult.Empty -> EmptyState(
                    title = stringResource(R.string.no_results_title),
                    message = stringResource(R.string.no_results_message),
                    emoji = "🔍",
                    modifier = Modifier.fillMaxSize()
                )

                is SearchResult.Error -> EmptyState(
                    title = stringResource(R.string.error_title),
                    message = stringResource(result.error.messageRes),
                    emoji = "⚠️",
                    modifier = Modifier.fillMaxSize(),
                    action = {
                        Button(onClick = onRetry) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.searching),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BookResults(
    books: List<OnlineBook>,
    addedKeys: Set<String>,
    onAddClick: (OnlineBook) -> Unit
) {
    val pagesFormat = stringResource(R.string.online_pages)
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books, key = { book -> book.key }) { book ->
            val details = listOfNotNull(
                book.year?.toString(),
                book.pages?.let { pagesFormat.format(it) }
            ).joinToString(" · ").ifEmpty { null }

            OnlineBookCard(
                title = book.title,
                author = book.author,
                details = details,
                coverUrl = book.coverUrl,
                isAdded = book.key in addedKeys,
                onAddClick = { onAddClick(book) }
            )
        }
    }
}

private val NetworkError.messageRes: Int
    get() = when (this) {
        NetworkError.NO_INTERNET -> R.string.error_no_internet
        NetworkError.SERVER -> R.string.error_server
        NetworkError.UNKNOWN -> R.string.error_unknown
    }

@Preview(showBackground = true)
@Composable
private fun OnlineSearchScreenPreview() {
    BookShelfTheme {
        OnlineSearchScreen(
            query = "Dostoevsky",
            result = SearchResult.Success(
                listOf(
                    OnlineBook(
                        key = "/works/OL1W", title = "Crime and Punishment",
                        author = "Fyodor Dostoevsky", year = 1866, pages = 551,
                        genre = "Fiction", coverUrl = null
                    )
                )
            ),
            addedKeys = emptySet(),
            onQueryChange = {},
            onSearch = {},
            onRetry = {},
            onAddClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnlineSearchErrorPreview() {
    BookShelfTheme {
        OnlineSearchScreen(
            query = "Dostoevsky",
            result = SearchResult.Error(NetworkError.NO_INTERNET),
            addedKeys = emptySet(),
            onQueryChange = {},
            onSearch = {},
            onRetry = {},
            onAddClick = {},
            onBackClick = {}
        )
    }
}

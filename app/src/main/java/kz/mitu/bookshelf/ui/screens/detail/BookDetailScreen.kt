package kz.mitu.bookshelf.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.ui.components.BookCover
import kz.mitu.bookshelf.ui.components.BookShelfTopBar
import kz.mitu.bookshelf.ui.components.EmptyState
import kz.mitu.bookshelf.ui.components.FilterChipRow
import kz.mitu.bookshelf.ui.components.RatingBar
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun BookDetailScreen(
    book: Book?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onStatusChange: (BookStatus) -> Unit,
    onRatingChange: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            BookShelfTopBar(
                title = stringResource(R.string.book_details),
                onBackClick = onBackClick,
                actions = {
                    if (book != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_book)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (book == null) {
            EmptyState(
                title = stringResource(R.string.book_not_found),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                BookCover(
                    title = book.title,
                    width = 140.dp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.by_author, book.author),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.book_meta, book.genre, book.pages),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingBar(
                    rating = book.rating,
                    starSize = 24.dp,
                    onRatingChange = onRatingChange,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle(text = stringResource(R.string.reading_status))
                FilterChipRow(
                    options = BookStatus.entries,
                    selected = book.status,
                    onSelected = { status -> if (status != null) onStatusChange(status) },
                    label = { status -> stringResource(status.labelRes) },
                    contentPadding = PaddingValues(0.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle(text = stringResource(R.string.about_book))
                val noDescription = stringResource(R.string.no_description)
                Text(
                    text = book.description.ifBlank { noDescription },
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.delete_book))
                }
            }
        }
    }

    if (showDeleteDialog && book != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete_book)) },
            text = { Text(text = stringResource(R.string.delete_book_question, book.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailScreenPreview() {
    BookShelfTheme {
        BookDetailScreen(
            book = Book(
                id = 1, title = "The Path of Abai", author = "Mukhtar Auezov",
                genre = "Historical novel", pages = 1100, rating = 5,
                status = BookStatus.FINISHED,
                description = "An epic about the life of the poet Abai Kunanbayev."
            ),
            onBackClick = {},
            onEditClick = {},
            onStatusChange = {},
            onRatingChange = {},
            onDeleteClick = {}
        )
    }
}

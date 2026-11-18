package kz.mitu.bookshelf.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.ui.components.BookShelfTopBar
import kz.mitu.bookshelf.ui.components.BookTextField
import kz.mitu.bookshelf.ui.components.FilterChipRow
import kz.mitu.bookshelf.ui.components.RatingBar
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun AddEditBookScreen(
    isEditMode: Boolean,
    title: String,
    author: String,
    genre: String,
    pages: String,
    rating: Int,
    status: BookStatus,
    description: String,
    titleError: Boolean,
    authorError: Boolean,
    pagesError: Boolean,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onPagesChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onStatusChange: (BookStatus) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requiredError = stringResource(R.string.error_required)

    Scaffold(
        modifier = modifier,
        topBar = {
            BookShelfTopBar(
                title = stringResource(if (isEditMode) R.string.edit_book else R.string.add_book),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookTextField(
                value = title,
                onValueChange = onTitleChange,
                label = stringResource(R.string.field_title),
                errorMessage = if (titleError) requiredError else null
            )
            BookTextField(
                value = author,
                onValueChange = onAuthorChange,
                label = stringResource(R.string.field_author),
                errorMessage = if (authorError) requiredError else null
            )
            BookTextField(
                value = genre,
                onValueChange = onGenreChange,
                label = stringResource(R.string.field_genre)
            )
            BookTextField(
                value = pages,
                onValueChange = { newValue -> onPagesChange(newValue.filter { it.isDigit() }.take(5)) },
                label = stringResource(R.string.field_pages),
                errorMessage = if (pagesError) stringResource(R.string.error_pages) else null,
                keyboardType = KeyboardType.Number
            )

            FormLabel(text = stringResource(R.string.reading_status))
            FilterChipRow(
                options = BookStatus.entries,
                selected = status,
                onSelected = { selected -> if (selected != null) onStatusChange(selected) },
                label = { option -> stringResource(option.labelRes) },
                contentPadding = PaddingValues(0.dp)
            )

            FormLabel(text = stringResource(R.string.field_rating))
            RatingBar(
                rating = rating,
                starSize = 28.dp,
                onRatingChange = onRatingChange
            )

            BookTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.field_description),
                singleLine = false,
                minLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun AddEditBookScreenPreview() {
    BookShelfTheme {
        AddEditBookScreen(
            isEditMode = false,
            title = "",
            author = "Abai Kunanbayev",
            genre = "Philosophy",
            pages = "abc",
            rating = 4,
            status = BookStatus.WANT_TO_READ,
            description = "",
            titleError = true,
            authorError = false,
            pagesError = true,
            onTitleChange = {},
            onAuthorChange = {},
            onGenreChange = {},
            onPagesChange = {},
            onRatingChange = {},
            onStatusChange = {},
            onDescriptionChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

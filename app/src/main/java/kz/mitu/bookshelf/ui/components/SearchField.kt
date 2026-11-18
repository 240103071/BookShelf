package kz.mitu.bookshelf.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

/**
 * Search box. It does not keep its own text: the screen passes [query]
 * and gets every change through [onQueryChange] (state hoisting).
 */
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = stringResource(R.string.search_books)) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.clear_search)
                    )
                }
            }
        },
        singleLine = true,
        shape = CircleShape
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchFieldPreview() {
    BookShelfTheme {
        SearchField(query = "Abai", onQueryChange = {}, modifier = Modifier.padding(16.dp))
    }
}

package kz.mitu.bookshelf.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun <T> FilterChipRow(
    options: List<T>,
    selected: T?,
    onSelected: (T?) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    allLabel: String? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        if (allLabel != null) {
            item {
                FilterChip(
                    selected = selected == null,
                    onClick = { onSelected(null) },
                    label = { Text(text = allLabel) }
                )
            }
        }
        items(options) { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelected(if (option == selected) null else option) },
                label = { Text(text = label(option)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChipRowPreview() {
    BookShelfTheme {
        FilterChipRow(
            options = listOf("Novel", "Poetry", "History"),
            selected = "Poetry",
            onSelected = {},
            label = { it },
            allLabel = "All"
        )
    }
}

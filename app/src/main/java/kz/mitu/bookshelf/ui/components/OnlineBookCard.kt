package kz.mitu.bookshelf.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun OnlineBookCard(
    title: String,
    author: String,
    details: String?,
    coverUrl: String?,
    isAdded: Boolean,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unknownAuthor = stringResource(R.string.unknown_author)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnlineCover(title = title, coverUrl = coverUrl)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = author.ifBlank { unknownAuthor },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (details != null) {
                    Text(
                        text = details,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isAdded) {
                    OutlinedButton(onClick = {}, enabled = false) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.in_library))
                    }
                } else {
                    FilledTonalButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.add_to_library))
                    }
                }
            }
        }
    }
}

@Composable
private fun OnlineCover(title: String, coverUrl: String?) {
    if (coverUrl == null) {
        BookCover(title = title, width = 64.dp)
    } else {
        val placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
        AsyncImage(
            model = coverUrl,
            contentDescription = title,
            modifier = Modifier
                .width(64.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.small),
            placeholder = placeholder,
            error = placeholder,
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnlineBookCardPreview() {
    BookShelfTheme {
        OnlineBookCard(
            title = "Crime and Punishment",
            author = "Fyodor Dostoevsky",
            details = "1866 · 551 pages",
            coverUrl = null,
            isAdded = false,
            onAddClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

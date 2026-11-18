package kz.mitu.bookshelf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

private val CoverColors = listOf(
    Color(0xFF7D5636),
    Color(0xFF5E6135),
    Color(0xFF3F5F73),
    Color(0xFF8C4A4A),
    Color(0xFF5B4B7A),
    Color(0xFF2F6B5B)
)

@Composable
fun BookCover(
    title: String,
    modifier: Modifier = Modifier,
    width: Dp = 56.dp
) {
    val color = CoverColors[title.hashCode().mod(CoverColors.size)]
    val initials = title
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.small)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            style = if (width >= 100.dp) {
                MaterialTheme.typography.headlineSmall
            } else {
                MaterialTheme.typography.titleMedium
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCoverPreview() {
    BookShelfTheme {
        Row {
            BookCover(title = "Abai Joly", modifier = Modifier.padding(8.dp))
            BookCover(title = "Clean Code", width = 120.dp, modifier = Modifier.padding(8.dp))
        }
    }
}

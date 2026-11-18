package kz.mitu.bookshelf.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun RatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: Dp = 16.dp,
    onRatingChange: ((Int) -> Unit)? = null
) {
    val description = stringResource(R.string.rating_value, rating, maxRating)

    Row(
        modifier = modifier.semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (star in 1..maxRating) {
            val tint = if (star <= rating) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
            if (onRatingChange != null) {
                IconButton(
                    onClick = { onRatingChange(if (star == rating) 0 else star) },
                    modifier = Modifier.size(starSize + 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(starSize)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingBarPreview() {
    BookShelfTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            RatingBar(rating = 4)
            RatingBar(rating = 3, starSize = 28.dp, onRatingChange = {})
        }
    }
}

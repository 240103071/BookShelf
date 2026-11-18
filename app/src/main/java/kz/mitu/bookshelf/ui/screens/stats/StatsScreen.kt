package kz.mitu.bookshelf.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.data.model.BookStatus
import kz.mitu.bookshelf.ui.components.BookShelfTopBar
import kz.mitu.bookshelf.ui.components.StatCard
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

@Composable
fun StatsScreen(
    totalBooks: Int,
    countByStatus: Map<BookStatus, Int>,
    pagesRead: Int,
    averageRating: Float,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BookShelfTopBar(
                title = stringResource(R.string.statistics),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    value = totalBooks.toString(),
                    label = stringResource(R.string.stats_total_books),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = pagesRead.toString(),
                    label = stringResource(R.string.stats_pages_read),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = stringResource(R.string.stats_average_value, averageRating),
                    label = stringResource(R.string.stats_average_rating),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.stats_by_status),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            BookStatus.entries.forEach { status ->
                val count = countByStatus[status] ?: 0
                val share = if (totalBooks > 0) count.toFloat() / totalBooks else 0f
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(status.labelRes),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { share },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsScreenPreview() {
    BookShelfTheme {
        StatsScreen(
            totalBooks = 6,
            countByStatus = mapOf(
                BookStatus.WANT_TO_READ to 2,
                BookStatus.READING to 1,
                BookStatus.FINISHED to 3
            ),
            pagesRead = 1524,
            averageRating = 3.0f,
            onBackClick = {}
        )
    }
}

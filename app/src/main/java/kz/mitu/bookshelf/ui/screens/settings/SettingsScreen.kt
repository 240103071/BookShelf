package kz.mitu.bookshelf.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.R
import kz.mitu.bookshelf.data.model.SortOption
import kz.mitu.bookshelf.ui.components.BookShelfTopBar
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

private enum class ThemeChoice(@StringRes val labelRes: Int, val darkTheme: Boolean?) {
    SYSTEM(R.string.theme_system, null),
    LIGHT(R.string.theme_light, false),
    DARK(R.string.theme_dark, true)
}

@Composable
fun SettingsScreen(
    darkTheme: Boolean?,
    sortOption: SortOption,
    onDarkThemeChange: (Boolean?) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BookShelfTopBar(
                title = stringResource(R.string.settings),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            SectionTitle(text = stringResource(R.string.settings_theme))
            Column(modifier = Modifier.selectableGroup()) {
                ThemeChoice.entries.forEach { choice ->
                    RadioRow(
                        label = stringResource(choice.labelRes),
                        selected = darkTheme == choice.darkTheme,
                        onClick = { onDarkThemeChange(choice.darkTheme) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SectionTitle(text = stringResource(R.string.settings_default_sort))
            Column(modifier = Modifier.selectableGroup()) {
                SortOption.entries.forEach { option ->
                    RadioRow(
                        label = stringResource(option.labelRes),
                        selected = sortOption == option,
                        onClick = { onSortOptionChange(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    BookShelfTheme {
        SettingsScreen(
            darkTheme = null,
            sortOption = SortOption.RECENTLY_ADDED,
            onDarkThemeChange = {},
            onSortOptionChange = {},
            onBackClick = {}
        )
    }
}

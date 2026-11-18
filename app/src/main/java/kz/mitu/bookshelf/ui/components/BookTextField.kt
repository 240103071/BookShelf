package kz.mitu.bookshelf.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.mitu.bookshelf.ui.theme.BookShelfTheme

/**
 * Text field used in forms. Shows [errorMessage] under the field in red
 * when it is not null.
 */
@Composable
fun BookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            { Text(text = errorMessage) }
        } else {
            null
        },
        singleLine = singleLine,
        minLines = minLines,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun BookTextFieldPreview() {
    BookShelfTheme {
        BookTextField(
            value = "",
            onValueChange = {},
            label = "Title",
            errorMessage = "This field is required",
            modifier = Modifier.padding(16.dp)
        )
    }
}

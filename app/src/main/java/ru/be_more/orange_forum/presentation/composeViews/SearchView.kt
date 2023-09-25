package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.theme.DvachTheme


@Composable
fun SearchView(modifier: Modifier = Modifier, args: SearchViewInitArgs) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(args.query))
    }

    Row (modifier = Modifier
        .background(MaterialTheme.colorScheme.secondary)
        .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .padding(16.dp, 8.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .weight(1f),
            value = text,
            singleLine = true,
            onValueChange = {
                text = it
                args.onTextChanged(it.text)
            },
            label = { args.label?.let { Text(it) } },
            placeholder = { args.placeholder?.let { Text(it) } },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
            )
        )
        DvachIcon(
            modifier = Modifier
                .size(40.dp)
                .clickable { text = TextFieldValue("") }
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(R.drawable.ic_baseline_clear_24)
        )
    }


}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode",
)
@Composable
fun SearchViewPreview() {
    DvachTheme(dynamicColor = false) {
        SearchView(
            args = SearchViewInitArgs(
                label = "null",
                placeholder = null,
                onTextChanged = {},
                onSearchClick = {}
            )
        )
    }
}

data class SearchViewInitArgs(
    val query: String = "",
    val placeholder: String? = null,
    val label: String? = null,
    val onTextChanged: (String) -> Unit,
    val onSearchClick: (String) -> Unit,
)
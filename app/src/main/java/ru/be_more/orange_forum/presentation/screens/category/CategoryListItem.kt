package ru.be_more.orange_forum.presentation.screens.category

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.presentation.data.ListItemArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun CategoryListItem(args: CategoryListItemViewInitArgs, modifier: Modifier = Modifier) {
    with (args) {
        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick(title) }
                .padding(16.dp, 16.dp),
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
fun CategoryListItemViewPreview() {
    DvachTheme(dynamicColor = false) {
        CategoryListItem(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            args = CategoryListItemViewInitArgs(
                title = "diy",
                onClick = {},
            )
        )
    }
}

data class CategoryListItemViewInitArgs(
    val title: String,
    val onClick: (String) -> Unit,
) : ListItemArgs
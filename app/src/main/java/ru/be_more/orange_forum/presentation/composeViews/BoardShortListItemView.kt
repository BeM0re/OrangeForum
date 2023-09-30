package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.data.ListItemArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun BoardShortListItemView(
    args: BoardShortListItemViewInitArgs,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    with (args) {
        Row(modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { onClick(title) }
        ) {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp, 16.dp)
            )
            Icon(painter = painterResource(
                id = R.drawable.ic_baseline_arrow_forward_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                )
        }
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
fun BoardShortListItemViewPreview() {
    DvachTheme(dynamicColor = false) {
        BoardShortListItemView(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            args = BoardShortListItemViewInitArgs(
                title = "diy",
            )
        ) {}
    }
}

data class BoardShortListItemViewInitArgs(
    val title: String,
) : ListItemArgs
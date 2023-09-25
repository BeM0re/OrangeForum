package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun ToolbarView(modifier: Modifier = Modifier, args: ToolbarInitArgs) {
    with(args) {
        Column(modifier = modifier
            .fillMaxWidth()
        ){
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 16.dp, 12.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isRefreshVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_baseline_refresh_24),
                        modifier = Modifier.padding(0.dp, 8.dp, 8.dp, 8.dp),
                    )

                if (isFavHollowVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_favorite_border_accent_24dp),
                        modifier = Modifier.padding(0.dp, 8.dp, 8.dp, 8.dp),
                    )

                if (isFavFilledVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_favorite_accent_24dp),
                        modifier = Modifier.padding(0.dp, 8.dp, 8.dp, 8.dp),
                    )

                if (isDownloadVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_cloud_download_accent_24dp),
                        modifier = Modifier.padding(0.dp, 8.dp, 8.dp, 8.dp),
                    )

                if (isDownloadedVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_cloud_done_black_24dp),
                        modifier = Modifier.padding(0.dp, 8.dp, 8.dp, 8.dp),
                    )
            }
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
fun ToolbarViewPreview() {
    DvachTheme(dynamicColor = false) {
        ToolbarView(
            args = ToolbarInitArgs(
                title = "Title asd asd asd asdasd asd Title asd asd asd asdasd Title asd asd asd asdasd",
                isRefreshVisible = true,
                isFavHollowVisible = true,
                isFavFilledVisible = true,
                isDownloadVisible = true,
                isDownloadedVisible = true,
            )
        )
    }
}

data class ToolbarInitArgs(
    val title: String,
    val isRefreshVisible: Boolean,
    val isFavHollowVisible: Boolean,
    val isFavFilledVisible: Boolean,
    val isDownloadVisible: Boolean,
    val isDownloadedVisible: Boolean,
)

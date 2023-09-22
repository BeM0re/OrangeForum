package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun CustomToolbar(modifier: Modifier = Modifier, args: ToolbarInitArgs) {
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
                )

                if (isRefreshVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_baseline_refresh_24),
                    )

                if (isFavHollowVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_favorite_border_accent_24dp),
                    )

                if (isFavFilledVisible)
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_favorite_accent_24dp),
                    )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode",
)
@Composable
fun Preview() {
    DvachTheme(dynamicColor = false) {
        CustomToolbar(
            args = ToolbarInitArgs(
                title = "Title",
                isRefreshVisible = true,
                isFavHollowVisible = true,
                isFavFilledVisible = true,
            )
        )
    }
}

data class ToolbarInitArgs(
    val title: String,
    val isRefreshVisible: Boolean,
    val isFavHollowVisible: Boolean,
    val isFavFilledVisible: Boolean,
)

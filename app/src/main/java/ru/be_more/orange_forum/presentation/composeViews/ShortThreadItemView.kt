package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.presentation.data.ShortThreadInitArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun ShortThreadItemView(
    args: ShortThreadInitArgs,
    onClick: (String, Int) -> Unit
) {
    with(args) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(boardId, threadNum) }
                .background(MaterialTheme.colorScheme.primary)
                .padding(32.dp, 8.dp, 16.dp, 8.dp)
                .height(32.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style =
                    if (isDrown) TextStyle(textDecoration = TextDecoration.LineThrough)
                    else LocalTextStyle.current,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )

            if (hasNewMessage)
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
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
fun ShortThreadItemViewPreview() {
    DvachTheme(dynamicColor = false) {
        ShortThreadItemView(
            args = ShortThreadInitArgs(
                boardId = "b",
                threadNum = 0,
                title = "Hfpyjt",
                isDrown = true,
                hasNewMessage = true,
            ),
            onClick = { _, _ -> },
        )
    }
}
package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OpPostShortView(args: OpPostShortInitArgs, modifier: Modifier = Modifier) {
    with(args) {
        Row(modifier
            .fillMaxWidth()
            .clickable { onClick(boardId, threadId) }
        ) {
            Box(Modifier.size(72.dp)) {
                thumbnailUrl?.let {
                    GlideImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onPic(picUrl) }
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    DvachIcon(
                        painter = painterResource(R.drawable.ic_play_arrow_accent_24dp),
                        modifier = Modifier.padding(24.dp)
                    )
                    if (messageCount > 0) Text(
                        text = messageCount.toString(),
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .padding(4.dp)
                    )
                }
            }
            Text(
                text = subject,
                modifier = Modifier.weight(1f)
            )
            DvachIcon(
                painter = painterResource(R.drawable.ic_delete_outline_24),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onDelete(boardId, threadId) }
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
fun OpPostShortPreview() {
    DvachTheme(dynamicColor = false) {
        OpPostShortView(
//            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            args = OpPostShortInitArgs(
                boardId = "",
                threadId = 123123,
                subject = "Thread title",
                thumbnailUrl = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg",
                picUrl = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg",
                messageCount = 12,
                isVideoAttached = true,
                onClick = { _, _ -> },
                onDelete = { _, _ -> },
                onPic = { },
            )
        )
    }
}

data class OpPostShortInitArgs(
    val boardId: String,
    val threadId: Int,
    val subject: String,
    val thumbnailUrl: String?,
    val picUrl: String,
    val messageCount: Int,
    val isVideoAttached: Boolean,
    val onClick: (String, Int) -> Unit,
    val onDelete: (String, Int) -> Unit,
    val onPic: (String) -> Unit,
)
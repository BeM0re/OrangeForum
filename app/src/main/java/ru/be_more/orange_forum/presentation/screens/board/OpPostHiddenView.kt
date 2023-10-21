package ru.be_more.orange_forum.presentation.screens.board

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.data.ListItemArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun OpPostHiddenView(
    args: HiddenOpPostInitArgs,
    modifier: Modifier = Modifier
) {
    with(args) {
        Text(
            text = post.subject.ifEmpty { post.comment },
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick(post.boardId, post.threadNum) }
                .padding(16.dp, 8.dp)
                .background(MaterialTheme.colorScheme.primary)
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
fun OpPostHiddenViewPreview() {
    DvachTheme(dynamicColor = false) {
        OpPostHiddenView(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            args = HiddenOpPostInitArgs(
                post = Post(
                    boardId = "diy",
                    id = 32323,
                    comment = stringResource(id = R.string.lorem),
                    postCount = 23,
                    fileCount = 12,
                    name = "Аноним",
                    timestamp = 12312312,
                    subject = stringResource(id = R.string.lorem),
                    files = listOf(
                        AttachedFile(thumbnail = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg"),
                        AttachedFile(thumbnail = "https://2ch.hk/diy/thumb/734711/16909101881910s.jpg"),
                    ),
                    date = "",
                    email = "",
                    isOpPost = true,
                    isAuthorOp = true,
                    number = 123,
                    threadNum = 12312
                ),
                onClick = {_, _, -> },
            )
        )
    }
}

data class HiddenOpPostInitArgs(
    val post: Post,
    val onClick: (String, Int) -> Unit,
) : ListItemArgs
package ru.be_more.orange_forum.presentation.screens.board

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.composeViews.CommentTextView
import ru.be_more.orange_forum.presentation.composeViews.ImageRow
import ru.be_more.orange_forum.presentation.data.ListItemArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun OpPostView(
    args: OpPostInitArgs,
    modifier: Modifier = Modifier,
    onViewThread: (String, Int) -> Unit,
) {
    with(args) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.name,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = post.dateTimeString,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = post.id.toString(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            if (post.subject.isNotEmpty()) Text(
                text = post.subject,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = W600,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            if (post.files.isNotEmpty()) ImageRow(
                files = post.files,
                onPic = onPick,
            )

            CommentTextView(
                text = post.comment,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp, 8.dp)
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(0.dp, 0.dp, 8.dp, 0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.missed_posts_title, post.postCount),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = W600,
                        maxLines = 2,
                        fontSize = 14.sp,
                        modifier = Modifier
                    )
                    Text(
                        text = stringResource(id = R.string.posts_with_image, post.fileCount),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier
                    )
                }
                TextButton(
                    onClick = { onHide(post.boardId, post.threadNum) }
                ) {
                    Text(
                        text = stringResource(id = R.string.btn_title_hide),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 14.sp,
                        fontWeight = W500,
                        modifier = Modifier.wrapContentSize()
                    )
                }
                TextButton(
                    onClick = { onViewThread(post.boardId, post.threadNum) }
                ) {
                    Text(
                        text = stringResource(id = R.string.btn_title_into),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 14.sp,
                        fontWeight = W500,
                        modifier = Modifier.wrapContentSize()
                    )
                }
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
fun OpPostViewPreview() {
    DvachTheme(dynamicColor = false) {
        OpPostView(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            onViewThread = {_, _ -> },
            args = OpPostInitArgs(
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
                onHide = {_, _, -> },
                onQueue = {_, _, -> },
                onPick = { }
            )
        )
    }
}

data class OpPostInitArgs(
    val post: Post,
    val onPick: (AttachedFile) -> Unit,
    val onHide: (String, Int) -> Unit,
    val onQueue: (String, Int) -> Unit,
) : ListItemArgs

//data class OpPostViewInitArgs(
//    val boardId: String,
//    val id: Int,
//    val text: String,
//    val name: String,
//    val dateTime: String,
//    val subject: String? = null,
//    val postCount: Int,
//    val imageCount: Int,
//    val images: List<AttachedFile> = emptyList(),
//    val onHide: (String, Int) -> Unit,
//    val onQueue: (String, Int) -> Unit,
//)
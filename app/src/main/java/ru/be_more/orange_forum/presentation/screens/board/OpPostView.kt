package ru.be_more.orange_forum.presentation.screens.board

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import ru.be_more.orange_forum.presentation.composeViews.DvachIcon
import ru.be_more.orange_forum.presentation.composeViews.ParsedTextView
import ru.be_more.orange_forum.presentation.composeViews.ImageRow
import ru.be_more.orange_forum.presentation.composeViews.initArgs.OpPostInitArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpPostView(
    args: OpPostInitArgs,
    modifier: Modifier = Modifier,
) {

    val width = 96.dp
    val squareSize = 48.dp

    var swipeableState = rememberDraggableState {

    }
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

    with(args) {

        Column(
            modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .clickable { onClick(post.boardId, post.threadNum) }
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

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (post.subject.isNotEmpty()) ParsedTextView(
                    text = post.subject,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = W600,
                    onTextClick = onTextLinkClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 0.dp, 16.dp, 0.dp),
                )

                DvachIcon(
                    painter = painterResource(
                        id = if(isQueued) R.drawable.ic_queue_added_accent_24 else R.drawable.ic_queue_add_accent_24
                    ),
                    modifier = Modifier
                        .clickable { onQueue(post.boardId, post.threadNum) }
                        .align(Alignment.CenterVertically)
                        .requiredSize(32.dp)
                )
            }

            if (post.files.isNotEmpty()) ImageRow(
                files = post.files,
                onPic = onPic,
            )

            ParsedTextView(
                text = post.comment,
                onTextClick = onTextLinkClick,
                maxLines = 3,
                isExpandable = true,
                modifier = Modifier
                    .padding(16.dp, 8.dp, 16.dp, 0.dp)
                    .combinedClickable(
                        onClick = { onClick(post.boardId, post.threadNum) },
                        onLongClick = { onHide(post.boardId, post.threadNum) },
                    ),
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
                    onClick = { onClick(post.boardId, post.threadNum) }
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
            args = OpPostInitArgs(
                post = Post(
                    boardId = "diy",
                    id = 32323,
                    comment = stringResource(id = R.string.lorem),
                    postCount = 23,
                    fileCount = 12,
                    name = "Аноним",
                    timestamp = 12312312,
                    subject = " asdaa sdasd as dasd asd asdsa dsas da sda sd asd asd asd asds ada sda sd asd asd asdsd as",
//                    subject = stringResource(id = R.string.lorem),
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
                isQueued = false,
                onHide = {_, _, -> },
                onQueue = {_, _, -> },
                onPic = { },
                onClick = { _, _ -> },
                onTextLinkClick = { },
            )
        )
    }
}


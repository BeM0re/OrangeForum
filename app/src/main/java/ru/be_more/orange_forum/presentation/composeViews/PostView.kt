package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun PostView(args: PostInitArgs, modifier: Modifier = Modifier) {
    with(args) {
        Column(modifier
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
                    text = post.number.toString(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                if (post.isAuthorOp) Text(
                    text = stringResource(id = R.string.op_check),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = post.name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = post.dateTimeString,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = post.id.toString(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            if (post.subject.isNotEmpty()) Text(
                text = post.subject,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = W500,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            if(post.files.isNotEmpty()) ImageRow(
                files = post.files,
                onPic = onPicClick,
                modifier = modifier
                    .fillMaxWidth()
            )

            CommentTextView(
                text = post.comment,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            Text(
                text = post.replies.mapIndexed { index, num ->
                    if (index == 0) ">>$num" else " >>$num" }
                    .toString()
                    .let {
                        it.substring(1, it.length - 1)
                    },
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
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
fun PostViewPreview() {
    DvachTheme(dynamicColor = false) {
        PostView(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            args = PostInitArgs(
                post = Post(
                    boardId = "",
                    threadNum = 123,
                    id = 123123,
                    name = "Anonim",
                    comment = stringResource(id = R.string.lorem),
                    isOpPost = true,
                    date = "02/04/20 Чтв 15:49:23",
                    email = "",
                    files = emptyList(),
                    fileCount = 0,
                    isAuthorOp = true,
                    postCount = 1,
                    subject = "Topic",
                    timestamp = 12312312,
                    number = 1233,
                    replies = listOf(12321, 32123),
                ),
                onPicClick = {},
            )
        )
    }
}



data class PostInitArgs(
    val post: Post,
    val onPicClick: (AttachedFile) -> Unit,
)

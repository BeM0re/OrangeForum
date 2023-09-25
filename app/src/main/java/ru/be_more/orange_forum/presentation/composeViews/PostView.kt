package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PostView(args: PostViewInitArgs, modifier: Modifier = Modifier) {
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
                    text = postNum.toString(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                if (isOp) Text(
                    text = stringResource(id = R.string.op_check),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = dateTime,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = id.toString(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            if (!subject.isNullOrEmpty()) Text(
                text = subject,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = W500,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 0.dp)
            ) {
                items(images) {
                    GlideImage(
                        model = it.thumbnail,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = text,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            Text(
                text = replies.mapIndexed { index, num ->
                    if (index == 0) ">>$num" else " >>$num" }
                    .toString()
                    .let {
                        it.substring(1, it.length - 1)
                    },
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
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
            args = PostViewInitArgs(
                id = 32323,
                text = stringResource(id = R.string.lorem),
                postNum = 22,
                isOp = true,
                name = "Аноним",
                dateTime = "02/04/20 Чтв 15:49:23",
                subject = stringResource(id = R.string.lorem),
                images = listOf(
                    AttachFile(thumbnail = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg"),
                    AttachFile(thumbnail = "https://2ch.hk/diy/thumb/734711/16909101881910s.jpg"),
                ),
                replies = listOf(12321, 32123),
            )
        )
    }
}

data class PostViewInitArgs(
    val id: Int,
    val text: String,
    val postNum: Int,
    val isOp: Boolean,
    val name: String,
    val dateTime: String,
    val subject: String? = null,
    val images: List<AttachFile> = emptyList(),
    val replies: List<Int> = emptyList(),
)
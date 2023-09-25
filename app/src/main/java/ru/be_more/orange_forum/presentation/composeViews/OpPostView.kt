package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
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
fun OpPostView(args: OpPostViewInitArgs, modifier: Modifier = Modifier) {
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
                fontWeight = W600,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )

            if (images.isNotEmpty()) LazyVerticalGrid(
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

            Row(Modifier.padding(16.dp, 8.dp)) {
                Column() {
                    Text(
                        text = stringResource(id = R.string.missed_posts_title, postCount),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = W600,
                        maxLines = 2,
                        modifier = Modifier
                    )
                    Text(
                        text = stringResource(id = R.string.posts_with_image, imageCount),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                    )
                }
                Text(
                    text = stringResource(id = R.string.btn_title_into),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = W500,
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize()
                        .clickable { onViewThread(boardId, id) }
                        .shadow(1.dp, shape = RoundedCornerShape(CornerSize(4.dp)))
                        .padding(16.dp, 8.dp)
                )
                Text(
                    text = stringResource(id = R.string.btn_title_hide),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = W500,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentWidth()
                        .clickable { onHide(boardId, id) }
                        .shadow(1.dp, shape = RoundedCornerShape(CornerSize(4.dp)))
                        .padding(16.dp, 8.dp)
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
fun OpPostViewPreview() {
    DvachTheme(dynamicColor = false) {
        OpPostView(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            args = OpPostViewInitArgs(
                boardId = "diy",
                id = 32323,
                text = stringResource(id = R.string.lorem),
                postCount = 23,
                imageCount = 12,
                name = "Аноним",
                dateTime = "02/04/20 Чтв 15:49:23",
                subject = stringResource(id = R.string.lorem),
                images = listOf(
                    AttachFile(thumbnail = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg"),
                    AttachFile(thumbnail = "https://2ch.hk/diy/thumb/734711/16909101881910s.jpg"),
                ),
                onViewThread = {_, _, ->},
                onHide = {_, _, ->},
            )
        )
    }
}

data class OpPostViewInitArgs(
    val boardId: String,
    val id: Int,
    val text: String,
    val name: String,
    val dateTime: String,
    val subject: String? = null,
    val postCount: Int,
    val imageCount: Int,
    val images: List<AttachFile> = emptyList(),
    val onViewThread: (String, Int) -> Unit,
    val onHide: (String, Int) -> Unit,
)
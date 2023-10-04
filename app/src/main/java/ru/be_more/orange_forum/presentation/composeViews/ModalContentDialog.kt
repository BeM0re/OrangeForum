package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun ModalContentDialog(args: ModalContentDialogInitArgs, modifier: Modifier = Modifier) {
    Dialog(
        onDismissRequest = { args.onDismiss() },
    ) {
        when (args.content) {
            is AttachedFile -> PicDialog(file = args.content)
            is Post -> PostDialog(post = args.content, onPicClick = args.onPicClick)
            is Attachment -> { /*todo delete*/ }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PicDialog(file: AttachedFile) {
    val scale = remember { mutableFloatStateOf(1f) }

    Box(
        Modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale.floatValue *= zoom
                }
            }
    ) {
        GlideImage(
            contentScale = ContentScale.FillWidth,
            model = file.getLink(isThumbnail = false),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .graphicsLayer(
                    scaleX = maxOf(1f, minOf(3f, scale.floatValue)),
                    scaleY = maxOf(1f, minOf(3f, scale.floatValue)),
                ),
        )

        Text(
            text = file.displayName,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PostDialog(
    post: Post,
    onPicClick: (AttachedFile) -> Unit,
) {
    Box(
        Modifier
            .clip(RectangleShape)
            .padding(24.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        PostView(args = PostInitArgs(
            post = post,
            onPicClick = onPicClick,
        ))
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
fun ModalContentDialogPreview() {
    DvachTheme(dynamicColor = false) {
        ModalContentDialog(
            ModalContentDialogInitArgs(
                /*content = AttachedFile(
                    displayName = "name.jpg",
                    thumbnail = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg",
                    path = "https://2ch.hk/diy/thumb/734711/16909098338470s.jpg",
                    duration = ""
                )*/
                content = Post(
                    boardId = "",
                    threadNum = 1,
                    id = 123,
                    name = "Anon",
                    comment = "text text",
                    isOpPost = true,
                    date = "21.21.21",
                    email = "",
                    fileCount = 0,
                    isAuthorOp = true,
                    postCount = 32,
                    subject = "Subject",
                    timestamp = 2312342232,
                    number = 312,
                ),
                onDismiss = {},
            )
        )
    }
}

data class ModalContentDialogInitArgs(
    val content: ModalContent,
    val onPicClick: (AttachedFile) -> Unit = { },
    val onDismiss: () -> Unit,
)
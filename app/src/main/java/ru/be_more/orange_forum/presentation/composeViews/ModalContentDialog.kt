package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.data.ImageInitArgs
import ru.be_more.orange_forum.presentation.data.ModalContentArgs
import ru.be_more.orange_forum.presentation.data.PostInitArgs
import ru.be_more.orange_forum.presentation.theme.DvachTheme
import ru.be_more.orange_forum.utils.GlideCookiedUrl

@Composable
fun ModalContentDialog(args: ModalContentDialogInitArgs, modifier: Modifier = Modifier) {
    with(args) {

        Dialog(
            onDismissRequest = { onClose() },
        ) {
            BackPressHandler(
                onBackPressed = { onBack() }
            )
            when (modalArgs) {
                is ImageInitArgs -> PicDialog(
                    args = modalArgs,
                    modifier = modifier,
                )
                is PostInitArgs -> PostDialog(
                    args = modalArgs,
                    modifier = modifier,
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PicDialog(
    args: ImageInitArgs,
    modifier: Modifier = Modifier,
) {
    val scale = remember { mutableFloatStateOf(1f) }
    val translationY = remember { mutableFloatStateOf(1f) }
    val translationX = remember { mutableFloatStateOf(1f) }

    Box(
        modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp, 24.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale.floatValue *= zoom
                    translationX.floatValue += pan.x
                    translationY.floatValue += pan.y
                }
            }
    ) {
        val url = GlideCookiedUrl.getGlideUrl(
            args.file.getLink(isThumbnail = false)
        )

        GlideImage(
            contentScale = ContentScale.FillWidth,
            model = url,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .graphicsLayer(
                    scaleX = maxOf(1f, minOf(3f, scale.floatValue)),
                    scaleY = maxOf(1f, minOf(3f, scale.floatValue)),
                    translationX = translationX.floatValue,
                    translationY = translationY.floatValue,
                ),
        )

        Text(
            text = args.file.displayName,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PostDialog(
    args: PostInitArgs,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RectangleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PostView(
            args = args
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
fun ModalContentDialogPreview() {
    DvachTheme(dynamicColor = false) {
        ModalContentDialog(
            ModalContentDialogInitArgs(
                modalArgs = PostInitArgs(
                    post = Post(
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
                    onTextLinkClick = { },
                    onPicClick = { }
                ),
                onBack = { },
                onClose = { },
            )
        )
    }
}

data class ModalContentDialogInitArgs(
    val modalArgs: ModalContentArgs,
    val onBack: () -> Unit,
    val onClose: () -> Unit,
)
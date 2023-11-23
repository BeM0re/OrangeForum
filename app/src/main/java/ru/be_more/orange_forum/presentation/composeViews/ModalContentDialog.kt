package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.sanghun.compose.video.RepeatMode
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ImageInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ModalContentArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.PostInitArgs
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

    var player: ExoPlayer

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

        if(args.file.duration.isEmpty()) {
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
        } else {
            VideoPlayer(
                mediaItems = listOf(
                    VideoPlayerMediaItem.NetworkMediaItem(
                        url = DVACH_ROOT_URL + args.file.path,
                        mimeType = MimeTypes.APPLICATION_MP4VTT,
                    )
                ),
                handleLifecycle = true,
                autoPlay = true,
                usePlayerController = true,
                enablePip = true,
                handleAudioFocus = true,
                controllerConfig = VideoPlayerControllerConfig(
                    showSpeedAndPitchOverlay = false,
                    showSubtitleButton = false,
                    showCurrentTimeAndTotalTime = true,
                    showBufferingProgress = false,
                    showForwardIncrementButton = true,
                    showBackwardIncrementButton = true,
                    showBackTrackButton = true,
                    showNextTrackButton = true,
                    showRepeatModeButton = true,
                    controllerShowTimeMilliSeconds = 5_000,
                    controllerAutoShow = true,
                    showFullScreenButton = false //todo
                ),
                volume = 1f,  // volume 0.0f to 1.0f
                repeatMode = RepeatMode.ONE,
                onCurrentTimeChanged = { // long type, current player time (millisec)
                    Log.d("CurrentTime", it.toString())
                },
                playerInstance = { // ExoPlayer instance (Experimental)
                    addAnalyticsListener(
                        object : AnalyticsListener {
                            // player logger
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            )
        }

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
                    onPicClick = { },
                    onPostNumClick = {  }
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
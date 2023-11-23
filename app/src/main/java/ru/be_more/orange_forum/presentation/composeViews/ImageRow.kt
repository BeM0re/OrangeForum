package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.presentation.theme.DvachTheme
import ru.be_more.orange_forum.utils.GlideCookiedUrl

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageRow(
    files: List<AttachedFile>,
    onPic: (AttachedFile) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(16.dp, 8.dp, 16.dp, 0.dp)

    ) {
        items(files) { file ->
            val url = GlideCookiedUrl.getGlideUrl(
                file.getLink(isThumbnail = true)
            )
            Box(modifier = Modifier
                .height(120.dp)
                .clickable {
                    onPic(file)
                }
            ) {
                GlideImage(
                    contentScale = ContentScale.FillWidth,
                    model = url,
                    contentDescription = null,
                )
                if (file.duration.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_arrow_accent_24dp),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp),
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_arrow_accent_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp),
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
fun ImageRowPreview() {
    DvachTheme(dynamicColor = false) {
        ImageRow(
            files = listOf(
                AttachedFile(
                    thumbnail = "/b/thumb/296267653/17006691256762s.jpg"
                )
            ),
            onPic = {},
        )
    }
}
package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.domain.model.AttachedFile

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageRow(
    files: List<AttachedFile>,
    onPic: (AttachedFile) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(16.dp, 8.dp, 16.dp, 0.dp)

    ) {
        items(files) { file ->
            GlideImage(
                contentScale = ContentScale.FillWidth,
                model = file.getLink(isThumbnail = true),
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .clickable {
                        onPic(file)
                    }
            )
        }
    }
}
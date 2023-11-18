package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun IconPickerView(
    title: String,
    url: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
            )

            Text(
                text = title,
                modifier = Modifier
                    .padding(16.dp, 0.dp)
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
fun IconPickerViewPreview() {
    DvachTheme(dynamicColor = false) {
        IconPickerView(
            title = "asd",
            url = "https://2ch.hk/static/icons/gsg/[4X] Age Of Wonders 3.png"
        )
    }
}
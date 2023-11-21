package ru.be_more.orange_forum.presentation.composeViews

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.theme.DvachTheme

@Composable
fun NavigationIcon(
    painter: Painter,
    isMarked: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Box (modifier){
        Icon(
            painter = painter,
            contentDescription = contentDescription,
        )
        if (isMarked)
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .align(Alignment.Center)
            )
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
fun NavigationIconPreview() {
    DvachTheme(dynamicColor = false) {
        NavigationIcon(
            painter = painterResource(id = R.drawable.ic_favorite_border_accent_24dp),
            isMarked = true,
            contentDescription = null,
        )
    }
}
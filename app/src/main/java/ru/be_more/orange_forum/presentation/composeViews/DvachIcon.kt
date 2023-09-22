package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun DvachIcon(painter: Painter, modifier: Modifier = Modifier,) {
    Icon(
        painter = painter,
        tint = MaterialTheme.colorScheme.tertiary,
        contentDescription = null,
        modifier = modifier
            .padding(0.dp, 8.dp, 8.dp, 8.dp)
    )
}
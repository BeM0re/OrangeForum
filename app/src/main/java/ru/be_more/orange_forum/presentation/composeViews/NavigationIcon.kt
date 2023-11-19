package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

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
                    .align(Alignment.Center)
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape)
            ) { }

    }
}
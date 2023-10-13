package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.presentation.data.TextLinkArgs

@Composable
fun ExpandableTextVew(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = 16.sp,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    isExpandedDefault: Boolean = false,
    onTextClick: (TextLinkArgs) -> Unit,
) {

    var isExpanded by rememberSaveable { mutableStateOf(isExpandedDefault) }

    ParsedTextView(
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = color,
        maxLines = if (!isExpanded) Int.MAX_VALUE else 4,
        modifier = modifier
            .clickable { isExpanded = !isExpanded },
        onTextClick = onTextClick,

    )
}
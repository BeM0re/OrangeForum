package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.presentation.screens.board.OpPostInitArgs
import ru.be_more.orange_forum.presentation.screens.board.OpPostView

@Composable
fun SwipableOpPost(
    args: OpPostInitArgs,
    modifier: Modifier = Modifier,
    onViewThread: (String, Int) -> Unit,
) {
    with(args) {
        val hide = SwipeAction(
            onSwipe = { onHide(post.boardId, post.id) },
            weight = 0.1,
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete chat",
                    modifier = Modifier.padding(16.dp),
                    tint = Color.White
                )
            },
            background = Color.Red.copy(alpha = 0.5f),
        )
        val queue = SwipeAction(
            onSwipe = { onQueue(post.boardId, post.id) },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_list_numbered_accent_24dp),
                    contentDescription = "archive chat",
                    modifier = Modifier.padding(16.dp),
                    tint = Color.White
                )
            },
            background = MaterialTheme.colorScheme.secondary
        )
        SwipeableActionsBox(
            swipeThreshold = 200.dp,
            startActions = listOf(hide),
            endActions = listOf(queue),
            modifier = modifier,
        ) {
            OpPostView(
                args = args,
                onViewThread = onViewThread,
            )
        }
    }
}
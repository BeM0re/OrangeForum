package ru.be_more.orange_forum.presentation.composeViews.initArgs

import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.Post

data class OpPostInitArgs(
    val post: Post,
    val isQueued: Boolean,
    val onPic: (AttachedFile) -> Unit,
    val onHide: (String, Int) -> Unit,
    val onQueue: (String, Int) -> Unit,
    val onTextLinkClick: (TextLinkArgs) -> Unit,
    val onClick: (String, Int) -> Unit,
) : ListItemArgs
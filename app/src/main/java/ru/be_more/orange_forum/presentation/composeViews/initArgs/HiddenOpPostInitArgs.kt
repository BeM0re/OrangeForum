package ru.be_more.orange_forum.presentation.composeViews.initArgs

import ru.be_more.orange_forum.domain.model.Post

data class HiddenOpPostInitArgs(
    val post: Post,
    val onClick: (String, Int) -> Unit,
) : ListItemArgs
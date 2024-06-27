package ru.be_more.ui.composeViews.initArgs

import ru.be_more.model.model.Post

data class HiddenOpPostInitArgs(
    val post: Post,
    val onClick: (String, Int) -> Unit,
) : ListItemArgs
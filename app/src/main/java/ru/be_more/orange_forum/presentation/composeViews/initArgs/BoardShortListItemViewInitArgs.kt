package ru.be_more.orange_forum.presentation.composeViews.initArgs

data class BoardShortListItemViewInitArgs(
    val id: String,
    val title: String,
    val onClick: (String) -> Unit,
) : ListItemArgs
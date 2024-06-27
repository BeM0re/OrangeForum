package ru.be_more.ui.composeViews.initArgs

data class BoardShortListItemViewInitArgs(
    val id: String,
    val title: String,
    val onClick: (String) -> Unit,
) : ListItemArgs
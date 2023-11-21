package ru.be_more.orange_forum.presentation.composeViews.initArgs

data class CategoryListItemViewInitArgs(
    val title: String,
    val onClick: (String) -> Unit,
) : ListItemArgs
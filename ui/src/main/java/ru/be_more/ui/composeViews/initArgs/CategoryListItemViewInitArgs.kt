package ru.be_more.ui.composeViews.initArgs

data class CategoryListItemViewInitArgs(
    val title: String,
    val onClick: (String) -> Unit,
) : ListItemArgs
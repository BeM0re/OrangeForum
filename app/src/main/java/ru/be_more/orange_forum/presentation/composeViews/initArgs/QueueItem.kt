package ru.be_more.orange_forum.presentation.composeViews.initArgs


sealed interface QueueItem

data class ShortBoardInitArgs(
    val boardId: String,
    val boardName: String,
    val onClick: (String) -> Unit,
) : QueueItem

data class ShortThreadInitArgs(
    val boardId: String,
    val threadNum: Int,
    val title: String,
    val isDrown: Boolean,
    val hasNewMessage: Boolean,
    val onClick: (String, Int) -> Unit,
) : QueueItem
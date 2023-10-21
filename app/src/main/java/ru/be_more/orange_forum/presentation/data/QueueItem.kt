package ru.be_more.orange_forum.presentation.data


sealed interface QueueItem

data class ShortBoardInitArgs(
    val boardId: String,
    val boardName: String,
) : QueueItem

data class ShortThreadInitArgs(
    val boardId: String,
    val threadNum: Int,
    val title: String,
    val isDrown: Boolean,
    val hasNewMessage: Boolean,
) : QueueItem
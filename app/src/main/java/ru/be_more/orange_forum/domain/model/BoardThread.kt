package ru.be_more.orange_forum.domain.model

data class BoardThread(
    val num : Int,
    val posts: List<Post> = listOf(),
    val title: String = "",
    val boardId: String,
    val lastPostNumber: Int = 0,
    val newMessageAmount: Int = 0,
    val postCount: Int,
    val fileCount: Int,
    val lasthit: Long, //timestamp последнего поста
    val isPinned: Boolean,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val isQueued: Boolean = false,
    val isDrown: Boolean = false,
    val hasNewMessages: Boolean = false,
)
package ru.be_more.orange_forum.domain.model

data class BoardThread(
    val num : Int,
    val posts: List<Post> = listOf(),
    val title: String = "",
    val boardId: String,
    val lastPostNumber: Int = 0,
    val newMessageAmount: Int = 0,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false, //todo delete?
    val isFavorite: Boolean = false,
    val isQueued: Boolean = false
) {
    fun isEmpty(): Boolean =
        num == -1
}
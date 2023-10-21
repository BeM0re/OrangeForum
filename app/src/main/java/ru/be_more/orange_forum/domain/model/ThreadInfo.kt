package ru.be_more.orange_forum.domain.model

data class ThreadInfo(
    val boardId: String,
    val threadNum: Int,
    val timestamp: Long? = null,
    val postCount: Int = 0,
    val isAlive: Boolean
)
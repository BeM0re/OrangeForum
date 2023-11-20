package ru.be_more.orange_forum.domain.model

data class ThreadInfo(
    val boardId: String,
    val threadNum: Int,
    val timestamp: Long = 0,
    val postCount: Int = 0,
    val isAlive: Boolean
)
package ru.be_more.orange_forum.data.remote.models

import ru.be_more.orange_forum.domain.model.ThreadInfo

data class ThreadInfoDto(
    val result: Int = 0,
    val thread: ThreadInfoInnerDto? = null
) {

    fun toModel(boardId: String, threadNum: Int): ThreadInfo =
        ThreadInfo(
            boardId = boardId,
            threadNum = threadNum,
            postCount = thread?.posts ?: 0,
            timestamp = thread?.timestamp,
            isAlive = result > 0,
        )

    data class ThreadInfoInnerDto(
        val num: Int,
        val posts: Int,
        val timestamp: Long,
    )
}
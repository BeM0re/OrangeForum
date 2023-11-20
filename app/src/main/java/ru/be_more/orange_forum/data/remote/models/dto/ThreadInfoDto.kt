package ru.be_more.orange_forum.data.remote.models.dto

import ru.be_more.orange_forum.domain.model.ThreadInfo

data class ThreadInfoDto(
    val result: Int = 0,
    val thread: ThreadInfoInnerDto? = null,
    val error: ThreadInfoErrorDto? = null,
) {

    fun toModel(boardId: String, threadNum: Int): ThreadInfo =
        ThreadInfo(
            boardId = boardId,
            threadNum = threadNum,
            postCount = thread?.posts ?: 0,
            timestamp = thread?.timestamp ?: 0,
            isAlive = result > 0,
        )

    data class ThreadInfoInnerDto(
        val num: Int,
        val posts: Int,
        val timestamp: Long,
    )

    //todo unify with other error dto?
    data class ThreadInfoErrorDto(
        val code: Int?,
        val message: String?,
    )
}
package ru.be_more.orange_forum.data.remote.models

data class ThreadInfoDto(
    val result: Int = 0,
    val thread: ThreadInfoInnerDto? = null
) {
    data class ThreadInfoInnerDto(
        val num: Int,
        val post: Int,
        val timestamp: Long,
    )
}
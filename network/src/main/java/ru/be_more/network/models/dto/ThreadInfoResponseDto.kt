package ru.be_more.network.models.dto

data class ThreadInfoResponseDto(
    val result: Int,
    val thread: ThreadInfoDto,
) {
    data class ThreadInfoDto(
        val num: Int,
        val posts: Int,
        val timestamp: Int,
    )
}
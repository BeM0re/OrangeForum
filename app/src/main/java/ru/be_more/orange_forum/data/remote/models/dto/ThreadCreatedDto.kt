package ru.be_more.orange_forum.data.remote.models.dto

data class ThreadCreatedDto(
    val result: Int,
    val error: ErrorDto?,
    val thread: Int?,
)
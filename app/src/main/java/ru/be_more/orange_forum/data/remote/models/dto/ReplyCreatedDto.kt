package ru.be_more.orange_forum.data.remote.models.dto

data class ReplyCreatedDto(
    val result: Int,
    val error: ErrorDto?,
    val num: Int?,
)
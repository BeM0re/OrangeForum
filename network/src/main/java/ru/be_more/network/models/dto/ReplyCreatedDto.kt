package ru.be_more.network.models.dto

data class ReplyCreatedDto(
    val result: Int,
    val error: ErrorDto?,
    val num: Int?,
)
package ru.be_more.network.models.dvach.dto

data class ReplyCreatedDto(
    val result: Int,
    val error: ErrorDto?,
    val num: Int?,
)
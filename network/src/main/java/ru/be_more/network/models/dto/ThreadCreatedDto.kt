package ru.be_more.network.models.dto

data class ThreadCreatedDto(
    val result: Int,
    val error: ErrorDto?,
    val thread: Int?,
)
package ru.be_more.network.models.dvach.dto

data class ThreadCreatedDto(
    val result: Int,
    val error: ErrorDto?,
    val thread: Int?,
)
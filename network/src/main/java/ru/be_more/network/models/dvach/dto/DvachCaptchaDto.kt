package ru.be_more.network.models.dvach.dto

data class DvachCaptchaDto(
    val id: String,
    val challenge: String,
    val input: String,
    val result: Int,
    val type: String,
)
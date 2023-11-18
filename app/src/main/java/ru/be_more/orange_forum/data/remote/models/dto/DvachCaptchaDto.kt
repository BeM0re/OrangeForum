package ru.be_more.orange_forum.data.remote.models.dto

import ru.be_more.orange_forum.data.remote.models.DvachCaptcha

data class DvachCaptchaDto(
    val id: String,
    val challenge: String,
    val input: String,
    val result: Int,
    val type: String,
) {
    fun toModel() =
        DvachCaptcha(
            id = id,
            challenge = challenge,
        )
}
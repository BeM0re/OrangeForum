package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.PostResponse
import ru.be_more.network.models.dvach.DvachCaptcha
import ru.be_more.network.models.dvach.dto.DvachCaptchaDto
import ru.be_more.network.models.dvach.dto.ResponseDto

fun ResponseDto.toModel(): PostResponse =
    PostResponse(
        error = error,
        status = status,
        num = num,
        reason = reason
    )

fun DvachCaptchaDto.toModel(): DvachCaptcha =
    DvachCaptcha(
        id = id,
        challenge = challenge,
    )

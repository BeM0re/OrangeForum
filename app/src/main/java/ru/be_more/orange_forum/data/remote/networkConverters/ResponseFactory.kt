package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.PostResponse
import ru.be_more.network.models.DvachCaptcha
import ru.be_more.network.models.dto.DvachCaptchaDto
import ru.be_more.network.models.dto.ResponseDto

object ResponseFactory {
    fun fromDto(responseDto: ResponseDto): PostResponse =
        PostResponse(
            error = responseDto.error,
            status = responseDto.status,
            num = responseDto.num,
            reason = responseDto.reason
        )

    fun fromDto(dvachCaptchaDto: DvachCaptchaDto): DvachCaptcha =
        DvachCaptcha(
            id = dvachCaptchaDto.id,
            challenge = dvachCaptchaDto.challenge,
        )
}
package ru.be_more.orange_forum.data.remote.models.dto

import ru.be_more.orange_forum.data.remote.models.BoardCaptchaSetting
import ru.be_more.orange_forum.data.remote.models.CaptureType

/**
 * @param result 3 - Капча не требуется. Например, в случае если на доске она отключена.
 * @param result 2 - Капча не требуется, поскольку активен VIP аккаунт.
 * @param result 1 - Запрос удовлетворён успешно.
 * @param result 0 - При выполнении запроса возникла ошибка.
 */
data class BoardCaptureSettingDto(
    val enabled: Int, //0 or 1
    val result: Int,
    val types: List<CaptchaType>
) {
    fun toModel() =
        BoardCaptchaSetting(
            isCaptchaEnabled = enabled > 0,
            captchaType =
                CaptureType
                    .values()
                    .firstOrNull { type ->
                        type.value in types.map { it.id }
                    } ?: CaptureType.NoCaptcha,
        )

    data class CaptchaType(
        val expires: Int,
        val id: String,
    )
}
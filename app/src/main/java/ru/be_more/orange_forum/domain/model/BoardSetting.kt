package ru.be_more.orange_forum.domain.model

data class BoardSetting(
    val isCaptchaEnabled: Boolean,
    val captchaType: CaptureType?,
) {
}
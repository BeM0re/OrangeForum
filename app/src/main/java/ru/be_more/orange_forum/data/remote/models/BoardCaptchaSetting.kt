package ru.be_more.orange_forum.data.remote.models

data class BoardCaptchaSetting(
    val isCaptchaEnabled: Boolean,
    val captchaType: CaptureType,
) {
}
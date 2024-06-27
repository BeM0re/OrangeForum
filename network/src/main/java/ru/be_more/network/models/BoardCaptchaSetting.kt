package ru.be_more.network.models

data class BoardCaptchaSetting(
    val isCaptchaEnabled: Boolean,
    val captchaType: CaptureType,
) {
}
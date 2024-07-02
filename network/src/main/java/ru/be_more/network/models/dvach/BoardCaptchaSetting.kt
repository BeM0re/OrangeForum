package ru.be_more.network.models.dvach

data class BoardCaptchaSetting(
    val isCaptchaEnabled: Boolean,
    val captchaType: CaptureType,
) {
}
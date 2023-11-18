package ru.be_more.orange_forum.data.remote.models

enum class CaptureType(
    val value: String,
) {
    DvachCaptcha("2chcaptcha"),
    Recaptcha("recaptcha"),
    InvisibleRecaptcha("invisible_recaptcha"),
    Recaptcha3("recaptcha3"),
    Appid("appid"),
    PassCode("passcode"),
    NoCaptcha("nocaptcha"),
}

/*
        Каждый тип капчи так же требует дополнительные параметры для её валидации:
        * recaptcha: g-recaptcha-response
        * invisible_recaptcha: g-recaptcha-response
        * recaptcha3: g-recaptcha-response
        * 2chcaptcha: Два.ч капча
        2chcaptcha_id - идентификатор Два.ч капчи.
        2chcaptcha_value - строка, которую пользователь увидел на картинке.
        * appid: app_response_id и app_response
        app_response_id - результат запроса к этому методу с публичным ключём приложения: /api/captcha/app/id/{public_key}
        app_response - sha256(app_response_id + '|' + private_key)
        * passcode: cookie passcode_auth
        * nocaptcha: капча не требуется, никакие дополнительные параметры тоже.
*/
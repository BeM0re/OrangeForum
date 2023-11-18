package ru.be_more.orange_forum.data.remote.models

sealed interface Captcha {
    val type: CaptureType
    val additionalFields: Map<String, String>
    fun solveCapture(value: String?)

    data class DvachCaptcha(
        override val type: CaptureType = CaptureType.DvachCaptcha,
        val id: String,
    ) : Captcha {
        override val additionalFields: Map<String, String>
            get() = mapOf(
                "2chcaptcha_id" to id,
                "2chcaptcha_value" to value
            )

        private var value: String = ""

        override fun solveCapture(value: String?) {
            this.value = value ?: ""
        }
    }

    data class NoCaptcha(
        override val type: CaptureType = CaptureType.DvachCaptcha,
        override val additionalFields: Map<String, String> = emptyMap()
    ) : Captcha {
        override fun solveCapture(value: String?) {}
    }
}
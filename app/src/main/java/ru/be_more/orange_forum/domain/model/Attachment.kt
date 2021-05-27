package ru.be_more.orange_forum.domain.model

import android.net.Uri

data class Attachment(
    val url: String? = null,
    val duration: String?,
    val uri: Uri? = null
) : ModalContent()
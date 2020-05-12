package ru.be_more.orange_forum.model

import android.net.Uri

data class Attachment(
    var url: String,
    var duration: String?,
    var uri: Uri? = null
) : ModalContent()
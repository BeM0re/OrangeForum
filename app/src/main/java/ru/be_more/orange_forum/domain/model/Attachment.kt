package ru.be_more.orange_forum.domain.model

import android.net.Uri

@Deprecated("")
data class Attachment( //todo delete
    val url: String? = null,
    val duration: String?,
    val uri: Uri? = null
) : ModalContent {
    constructor(file: AttachedFile) : this(
        url = file.path,
        duration = file.duration,
        uri = Uri.parse(file.localPath),
    )
}
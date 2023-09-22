package ru.be_more.orange_forum.domain.model

data class AttachFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tnHeight: Int = 0,
    val tnWidth: Int = 0,
    val path: String = "",
    val thumbnail: String = "",
    val duration : String = "",
    val localPath: String = "",
    val localThumbnail: String = "",
)
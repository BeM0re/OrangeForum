package ru.be_more.orange_forum.domain.model

data class AttachFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tn_height: Int = 0,
    val tn_width: Int = 0,
    val path: String = "",
    val thumbnail: String = "",
    val duration : String = "",
    val localPath: String = "",
    val localThumbnail: String = ""
)
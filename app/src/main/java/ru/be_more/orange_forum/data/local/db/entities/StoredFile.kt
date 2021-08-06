package ru.be_more.orange_forum.data.local.db.entities

data class StoredFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tn_height: Int = 0,
    val tn_width: Int = 0,
    val webPath: String,
    val localPath: String = "",
    val webThumbnail: String = "",
    val localThumbnail: String = "",
    val duration : String = ""
)

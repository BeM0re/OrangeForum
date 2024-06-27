package ru.be_more.database.db.entities

data class StoredFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tnHeight: Int = 0,
    val tnWidth: Int = 0,
    val webPath: String,
    val pathFullLink: String,
    val localPath: String? = null,
    val webThumbnail: String = "",
    val thumbnailFullLink: String,
    val localThumbnail: String? = null,
    val duration : String = ""
)

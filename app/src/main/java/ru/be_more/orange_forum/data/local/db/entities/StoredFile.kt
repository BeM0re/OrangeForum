package ru.be_more.orange_forum.data.local.db.entities

import ru.be_more.orange_forum.domain.model.AttachedFile

data class StoredFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tnHeight: Int = 0,
    val tnWidth: Int = 0,
    val webPath: String,
    val localPath: String? = null,
    val webThumbnail: String = "",
    val localThumbnail: String? = null,
    val duration : String = ""
) {
    constructor(file: AttachedFile): this(
        displayName = file.displayName,
        height = file.height,
        width = file.width,
        tnHeight = file.tnHeight,
        tnWidth = file.tnWidth,
        webPath = file.path,
        localPath = file.localPath,
        webThumbnail = file.thumbnail,
        localThumbnail = file.localThumbnail,
        duration = file.duration,
    )

    fun toModel() = AttachedFile(
        displayName = displayName,
        height = height,
        width = width,
        tnHeight = tnHeight,
        tnWidth = tnWidth,
        path = webPath,
        localPath = localPath,
        thumbnail = webThumbnail,
        localThumbnail = localThumbnail,
        duration = duration,
    )
}

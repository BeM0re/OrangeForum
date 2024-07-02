package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredFile
import ru.be_more.model.model.AttachedFile

fun StoredFile.toModel(): AttachedFile =
    AttachedFile(
        displayName = displayName,
        height = height,
        width = width,
        tnHeight = tnHeight,
        tnWidth = tnWidth,
        path = webPath,
        pathFullLink = pathFullLink,
        localPath = localPath,
        thumbnail = webThumbnail,
        thumbnailFullLink = thumbnailFullLink,
        localThumbnail = localThumbnail,
        duration = duration,
    )

fun AttachedFile.toEntity() =
    StoredFile(
        displayName = displayName,
        height = height,
        width = width,
        tnHeight = tnHeight,
        tnWidth = tnWidth,
        webPath = path,
        pathFullLink = pathFullLink,
        localPath = localPath,
        webThumbnail = thumbnail,
        thumbnailFullLink = thumbnailFullLink,
        localThumbnail = localThumbnail,
        duration = duration,
    )
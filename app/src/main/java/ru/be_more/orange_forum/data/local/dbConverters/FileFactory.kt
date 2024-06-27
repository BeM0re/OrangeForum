package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredFile
import ru.be_more.model.model.AttachedFile

object FileFactory {

    fun fromEntity(storedFile: StoredFile): AttachedFile =
        AttachedFile(
            displayName = storedFile.displayName,
            height = storedFile.height,
            width = storedFile.width,
            tnHeight = storedFile.tnHeight,
            tnWidth = storedFile.tnWidth,
            path = storedFile.webPath,
            pathFullLink = storedFile.pathFullLink,
            localPath = storedFile.localPath,
            thumbnail = storedFile.webThumbnail,
            thumbnailFullLink = storedFile.thumbnailFullLink,
            localThumbnail = storedFile.localThumbnail,
            duration = storedFile.duration,
        )

    fun toEntity(file: AttachedFile) =
        StoredFile(
            displayName = file.displayName,
            height = file.height,
            width = file.width,
            tnHeight = file.tnHeight,
            tnWidth = file.tnWidth,
            webPath = file.path,
            pathFullLink = file.pathFullLink,
            localPath = file.localPath,
            webThumbnail = file.thumbnail,
            thumbnailFullLink = file.thumbnailFullLink,
            localThumbnail = file.localThumbnail,
            duration = file.duration,
        )

}
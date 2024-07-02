package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.network.models.dvach.dto.FileDto
import ru.be_more.model.model.AttachedFile
import ru.be_more.model.model.Imageboard
import ru.be_more.network.models.fourchan.dto.FourchanPostDto

fun FileDto.toModel(imageboard: Imageboard): AttachedFile =
    AttachedFile(
        path = path,
        pathFullLink = imageboard.attachmentUrl + path,
        thumbnail = thumbnail,
        thumbnailFullLink = imageboard.attachmentUrl + thumbnail,
        duration = duration ?: "",
    )

fun FourchanPostDto.toAttachedFile(imageboard: Imageboard, boardId: String): AttachedFile? {

    if (fileId == null) return null

    return AttachedFile(
        path = "",
        pathFullLink = imageboard.attachmentUrl + "/" + boardId + "/" + fileId + fileExtension,
        thumbnail = "",
        thumbnailFullLink = imageboard.attachmentUrl + "/" + boardId + "/" + fileId + "s.jpg",
        duration = if (fileExtension == ".webm") "1" else "",
    )
}
package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.network.models.dto.FileDto
import ru.be_more.model.model.AttachedFile
import ru.be_more.model.model.BaseUrl

object FileFactory {
    fun fromDto(fileDto: FileDto, baseUrl: BaseUrl): AttachedFile =
        AttachedFile(
            path = fileDto.path,
            pathFullLink = baseUrl.url + fileDto.path,
            thumbnail = fileDto.thumbnail,
            thumbnailFullLink = baseUrl.url + fileDto.thumbnail,
            duration = fileDto.duration ?: "",
        )
}
package ru.be_more.orange_forum.domain.model

import ru.be_more.orange_forum.consts.DVACH_ROOT_URL

data class AttachedFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tnHeight: Int = 0,
    val tnWidth: Int = 0,
    val path: String = "",
    val thumbnail: String = "",
    val duration : String = "",
    val localPath: String? = null,
    val localThumbnail: String? = null,
) : ModalContent{
    fun getLink(isThumbnail: Boolean) =
        if (isThumbnail) localPath ?: (DVACH_ROOT_URL + thumbnail)
        else localThumbnail ?: (DVACH_ROOT_URL + path)
}
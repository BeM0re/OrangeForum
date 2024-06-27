package ru.be_more.model.model

data class AttachedFile(
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tnHeight: Int = 0,
    val tnWidth: Int = 0,
    val path: String = "",
    val pathFullLink: String = "",
    val thumbnail: String = "",
    val thumbnailFullLink: String = "",
    val duration : String = "",
    val localPath: String? = null,
    val localThumbnail: String? = null,
) : ModalContent {
    fun getLink(isThumbnail: Boolean) =
        if (isThumbnail) localPath ?: thumbnailFullLink
        else localThumbnail ?: pathFullLink
}
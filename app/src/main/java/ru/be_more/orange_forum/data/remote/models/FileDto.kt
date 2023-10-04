package ru.be_more.orange_forum.data.remote.models

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.AttachedFile

data class FileDto(
    @SerializedName("displayname")
    val displayName: String,
    val height: Int,
    val width: Int,
    @SerializedName("tn_height")
    val tnHeight: Int,
    @SerializedName("tn_width")
    val tnWidth: Int,
    val path: String,
    val thumbnail: String,
    val duration: String? = ""
) {
    fun toModel() = AttachedFile(
        path = path,
        thumbnail = thumbnail,
        duration = duration ?: ""
    )
}
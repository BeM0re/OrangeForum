package ru.be_more.network.models.dvach.dto

import com.google.gson.annotations.SerializedName

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
)
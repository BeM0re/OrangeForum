package ru.be_more.network.models.fourchan.dto

import com.google.gson.annotations.SerializedName

data class FourchanPostDto(
    val no: Int,
    val name: String,
    val com: String? = null,
    val now: String,
    val replies: Int? = null,
    val images: Int? = null,
    val sub: String? = null,
    val number: Int, //order number 0 - 500(1000)
    val sticky: Int = 0,
    val closed: Int = 0, //1 = cant reply into
    @SerializedName("last_modified")
    val lastModified: Long,
    @SerializedName("time")
    val timestamp: Long,
    @SerializedName("tim")
    val fileId: Long? = null,
    val fileName: String? = null,
    @SerializedName("ext")
    val fileExtension: String? = null,
)

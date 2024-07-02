package ru.be_more.network.models.dvach.dto

import com.google.gson.annotations.SerializedName

data class PostDto(
    val num: Int,
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files: List<FileDto>? = null,
    @SerializedName("files_count")
    val filesCount: Int? = null,
    val op: Int,
    @SerializedName("posts_count")
    val postsCount: Int? = null,
    val subject: String,
    val timestamp: Long,
    val number: Int, //order number 0 - 500(1000)
    val sticky: Int,
    val lasthit: Long,
)

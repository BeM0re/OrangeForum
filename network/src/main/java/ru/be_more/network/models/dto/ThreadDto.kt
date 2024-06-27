package ru.be_more.network.models.dto

import com.google.gson.annotations.SerializedName

data class ThreadDto(
    @SerializedName("current_thread")
    val num: Int,
    @SerializedName("posts_count")
    val postCount: Int = 0,
    @SerializedName("max_mun")
    val lastPostNumber: Int = 0,
    @SerializedName("files_count")
    val fileCount: Int = 0,
    val title: String = "",
    @SerializedName("threads")
    val threads: List<InnerThreadDto>
) {
    data class InnerThreadDto(
        val posts: List<PostDto> = emptyList()
    )
}
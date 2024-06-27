package ru.be_more.network.models.dto

import com.google.gson.annotations.SerializedName

data class BoardDto(
    val board: InnerBoardDto,
    val threads: List<PostDto> = listOf()
) {
    data class InnerBoardDto(
        @SerializedName("category")             val category: String = "",
        @SerializedName("name")                 val name: String = "",
        @SerializedName("enable_names")         val isNameEnabled: Boolean = false,
        @SerializedName("enable_trips")         val isTripEnabled: Boolean = false,
        @SerializedName("enable_subject")       val isSubjectEnabled: Boolean = false,
        @SerializedName("enable_sage")          val isSageEnabled: Boolean = false,
        @SerializedName("enable_icons")         val isIconEnabled: Boolean = false,
        @SerializedName("enable_flags")         val isFlagEnabled: Boolean = false,
        @SerializedName("enable_thread_tags")   val isTagEnabled: Boolean = false,
        @SerializedName("enable_posting")       val isPostingEnabled: Boolean = false,
        @SerializedName("enable_likes")         val isLikeEnabled: Boolean = false,
        @SerializedName("file_types")           val fileTypes: List<String>?,
        @SerializedName("max_comment")          val maxCommentSize: Int?,
        @SerializedName("max_files_size")       val maxFileSize: Int?,
        @SerializedName("tags")                 val tags: List<String>?,
        @SerializedName("icons")                val icons: List<IconDto>?,
    )

    data class IconDto(
        val num: Int,
        val name: String,
        val url: String,
    )
}
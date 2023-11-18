package ru.be_more.orange_forum.data.remote.models.dto

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardSetting
import ru.be_more.orange_forum.domain.model.Icon

data class BoardDto(
    val board: InnerBoardDto,
    val threads: List<PostDto> = listOf()
) {
    fun toModel(boardId: String) = Board(
        name = board.name,
        id = boardId,
        category = board.category,
        threads = threads.map { it.toThread(boardId) },
        isFavorite = false,
        boardSetting = BoardSetting(
            isNameEnabled = board.isNameEnabled,
            isTripEnabled = board.isTripEnabled,
            isSubjectEnabled = board.isSubjectEnabled,
            isSageEnabled = board.isSageEnabled,
            isIconEnabled = board.isIconEnabled,
            isFlagEnabled = board.isFlagEnabled,
            isPostingEnabled = board.isPostingEnabled,
            isLikeEnabled = board.isLikeEnabled,
            isTagEnabled = board.isTagEnabled,
            fileTypes = board.fileTypes ?: emptyList(),
            maxCommentSize = board.maxCommentSize,
            maxFileSize = board.maxFileSize,
            tags = board.tags,
            icons = board.icons?.map { it.toModel() },
        )
    )

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
    ) {
        fun toModel() = Icon(
            id = num,
            name = name,
            url = DVACH_ROOT_URL + url,
        )
    }
}
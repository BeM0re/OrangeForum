package ru.be_more.orange_forum.data.remote.models.dto

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardSetting

data class BoardShortDto(
    @SerializedName("default_name") val defaultName : String,
    val category : String,
    val id : String,
    val name : String
    //todo icons
) {
    fun toModel(): Board =
        Board(
            name = name,
            id = id,
            category = category,
            isFavorite = false,
            boardSetting = BoardSetting(
                isNameEnabled = false,
                isTripEnabled = false,
                isSubjectEnabled = false,
                isSageEnabled = false,
                isIconEnabled = false,
                isFlagEnabled = false,
                isPostingEnabled = false,
                isTagEnabled = false,
                isLikeEnabled = false,
                fileTypes = emptyList(),
                maxCommentSize = null,
                maxFileSize = null,
                tags = null,
                icons = null,
            )
        )
}


package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.converters.JsonRoomConverter
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardSetting
import ru.be_more.orange_forum.domain.model.Icon

@Entity(tableName = "boards")
@TypeConverters(JsonRoomConverter::class)
data class StoredBoard(
    @PrimaryKey val id: String,
    val name: String,
    val category: String = "",
    val isFavorite: Boolean,
    val isNameEnabled: Boolean,
    val isTripEnabled: Boolean,
    val isSubjectEnabled: Boolean,
    val isSageEnabled: Boolean,
    val isIconEnabled: Boolean,
    val isFlagEnabled: Boolean,
    val isTagEnabled: Boolean,
    val isPostingEnabled: Boolean,
    val isLikeEnabled: Boolean,
    val fileTypes: List<String>?,
    val maxCommentSize: Int?,
    val maxFileSize: Int?,
    val tags: List<String>?,
    val icons: List<Icon>?,
) {
    constructor(board: Board): this(
        id = board.id,
        name = board.name,
        category = board.category,
        isFavorite = board.isFavorite,
        isNameEnabled = board.boardSetting.isNameEnabled,
        isTripEnabled = board.boardSetting.isTripEnabled,
        isSubjectEnabled = board.boardSetting.isSubjectEnabled,
        isSageEnabled = board.boardSetting.isSageEnabled,
        isIconEnabled = board.boardSetting.isIconEnabled,
        isTagEnabled = board.boardSetting.isTagEnabled,
        isFlagEnabled = board.boardSetting.isFlagEnabled,
        isPostingEnabled = board.boardSetting.isPostingEnabled,
        isLikeEnabled = board.boardSetting.isLikeEnabled,
        fileTypes = board.boardSetting.fileTypes,
        maxCommentSize = board.boardSetting.maxCommentSize,
        maxFileSize = board.boardSetting.maxFileSize,
        tags = board.boardSetting.tags,
        icons = board.boardSetting.icons,
    )

    fun toModel() =
        Board(
            name = name,
            id = id,
            category = category,
            threads = emptyList(),
            isFavorite = isFavorite,
            boardSetting = BoardSetting(
                isNameEnabled = isNameEnabled,
                isTripEnabled = isTripEnabled,
                isSubjectEnabled = isSubjectEnabled,
                isSageEnabled = isSageEnabled,
                isIconEnabled = isIconEnabled,
                isFlagEnabled = isFlagEnabled,
                isTagEnabled = isTagEnabled,
                isPostingEnabled = isPostingEnabled,
                isLikeEnabled = isLikeEnabled,
                fileTypes = fileTypes,
                maxCommentSize = maxCommentSize,
                maxFileSize = maxFileSize,
                tags = tags,
                icons = icons,
            )
        )
}
package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredBoard
import ru.be_more.database.db.entities.StoredIcon
import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardSetting
import ru.be_more.model.model.Icon

object BoardFactory {

    fun fromEntity(storedBoard: StoredBoard): Board =
        Board(
            name = storedBoard.name,
            id = storedBoard.id,
            category = storedBoard.category,
            threads = emptyList(),
            isFavorite = storedBoard.isFavorite,
            boardSetting = BoardSetting(
                isNameEnabled = storedBoard.isNameEnabled,
                isTripEnabled = storedBoard.isTripEnabled,
                isSubjectEnabled = storedBoard.isSubjectEnabled,
                isSageEnabled = storedBoard.isSageEnabled,
                isIconEnabled = storedBoard.isIconEnabled,
                isFlagEnabled = storedBoard.isFlagEnabled,
                isTagEnabled = storedBoard.isTagEnabled,
                isPostingEnabled = storedBoard.isPostingEnabled,
                isLikeEnabled = storedBoard.isLikeEnabled,
                fileTypes = storedBoard.fileTypes,
                maxCommentSize = storedBoard.maxCommentSize,
                maxFileSize = storedBoard.maxFileSize,
                tags = storedBoard.tags,
                icons = storedBoard.icons?.map { fromEntity(it) },
            )
        )

    fun toEntity(board: Board) =
        StoredBoard(
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
            icons = board.boardSetting.icons?.map { toEntity(it) },
        )

    private fun fromEntity(storedIcon: StoredIcon) =
        Icon(
            id = storedIcon.id,
            name = storedIcon.name,
            url = storedIcon.url,
        )

    private fun toEntity(icon: Icon) =
        StoredIcon(
            id = icon.id,
            name = icon.name,
            url = icon.url,
        )
}
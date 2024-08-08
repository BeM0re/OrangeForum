package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredBoard
import ru.be_more.database.db.entities.StoredIcon
import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardSetting
import ru.be_more.model.model.Icon
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType

fun StoredBoard.toModel(): Board =
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
            icons = icons?.map { it.toModel() },
        ),
        imageboardType = ImageboardType.valueOf(imageboardType),
    )

fun Board.toEntity() =
    StoredBoard(
        id = id,
        name = name,
        category = category,
        isFavorite = isFavorite,
        isNameEnabled = boardSetting.isNameEnabled,
        isTripEnabled = boardSetting.isTripEnabled,
        isSubjectEnabled = boardSetting.isSubjectEnabled,
        isSageEnabled = boardSetting.isSageEnabled,
        isIconEnabled = boardSetting.isIconEnabled,
        isTagEnabled = boardSetting.isTagEnabled,
        isFlagEnabled = boardSetting.isFlagEnabled,
        isPostingEnabled = boardSetting.isPostingEnabled,
        isLikeEnabled = boardSetting.isLikeEnabled,
        fileTypes = boardSetting.fileTypes,
        maxCommentSize = boardSetting.maxCommentSize,
        maxFileSize = boardSetting.maxFileSize,
        tags = boardSetting.tags,
        icons = boardSetting.icons?.map { it.toEntity() },
        imageboardType = imageboardType.name,
    )

fun StoredIcon.toModel() =
    Icon(
        id = id,
        name = name,
        url = url,
    )

fun Icon.toEntity() =
    StoredIcon(
        id = id,
        name = name,
        url = url,
    )
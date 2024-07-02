package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardSetting
import ru.be_more.model.model.Icon
import ru.be_more.model.model.Imageboard
import ru.be_more.network.models.dvach.BoardCaptchaSetting
import ru.be_more.network.models.dvach.CaptureType
import ru.be_more.network.models.dvach.dto.BoardCaptureSettingDto
import ru.be_more.network.models.dvach.dto.BoardDto
import ru.be_more.network.models.dvach.dto.BoardDto.IconDto
import ru.be_more.network.models.dvach.dto.BoardShortDto
import ru.be_more.network.models.fourchan.dto.FourchanBoardPageDto
import ru.be_more.network.models.fourchan.dto.FourchanShortBoardDto
import ru.be_more.orange_forum.consts.FOURCHAN_CATEGORY

fun List<FourchanBoardPageDto>.toModel(boardName: String, boardId: String, imageboard: Imageboard): Board =
    Board(
        name = boardName,
        id = boardId,
        category = FOURCHAN_CATEGORY,
        threads = this
            .map { it.threads }
            .flatten()
            .map { it.toThread(boardId, imageboard) },
        isFavorite = false,
        boardSetting = BoardSetting(
            isNameEnabled = false,
            isTripEnabled = false,
            isSubjectEnabled = false,
            isSageEnabled = false,
            isIconEnabled = false, //todo
            isFlagEnabled = false, //todo
            isPostingEnabled = false, //todo
            isLikeEnabled = false,
            isTagEnabled = false,
            fileTypes = emptyList(),
            maxCommentSize = 500, //todo?
            maxFileSize = 1, //todo
            tags = emptyList(),
            icons = emptyList(),
        ),
        imageboard = imageboard,
    )

fun BoardDto.toModel(boardId: String, imageboard: Imageboard): Board =
    Board(
        name = board.name,
        id = boardId,
        category = board.category,
        threads = threads.map { it.toThread(boardId, imageboard) },
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
            icons = board.icons?.map { it.toModel(imageboard) },
        ),
        imageboard = imageboard,
    )

fun BoardShortDto.toModel(imageboard: Imageboard): Board =
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
        ),
        imageboard = imageboard,
    )

fun FourchanShortBoardDto.toModel(imageboard: Imageboard): Board =
    Board(
        name = title,
        id = board,
        category = FOURCHAN_CATEGORY,
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
        ),
        imageboard = imageboard,
    )


fun BoardCaptureSettingDto.toModel(): BoardCaptchaSetting =
    BoardCaptchaSetting(
        isCaptchaEnabled = enabled > 0,
        captchaType =
        CaptureType
            .entries
            .firstOrNull { type ->
                type.value in types.map { it.id }
            } ?: CaptureType.NoCaptcha,
    )

fun IconDto.toModel(imageboard: Imageboard) =
    Icon(
        id = num,
        name = name,
        url = imageboard.staticDataUrl + url,
    )
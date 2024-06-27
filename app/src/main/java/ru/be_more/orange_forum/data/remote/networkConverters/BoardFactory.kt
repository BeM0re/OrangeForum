package ru.be_more.orange_forum.data.remote.networkConverters

import ru.be_more.model.model.BaseUrl
import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardSetting
import ru.be_more.model.model.Icon
import ru.be_more.network.models.BoardCaptchaSetting
import ru.be_more.network.models.CaptureType
import ru.be_more.network.models.dto.BoardCaptureSettingDto
import ru.be_more.network.models.dto.BoardDto
import ru.be_more.network.models.dto.BoardDto.IconDto
import ru.be_more.network.models.dto.BoardShortDto
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL

object BoardFactory {

    fun fromDto(boardDto: BoardDto, boardId: String, baseUrl: BaseUrl): Board =
        Board(
            name = boardDto.board.name,
            id = boardId,
            category = boardDto.board.category,
            threads = boardDto.threads.map { PostFactory.toThread(it, boardId, baseUrl) },
            isFavorite = false,
            boardSetting = BoardSetting(
                isNameEnabled = boardDto.board.isNameEnabled,
                isTripEnabled = boardDto.board.isTripEnabled,
                isSubjectEnabled = boardDto.board.isSubjectEnabled,
                isSageEnabled = boardDto.board.isSageEnabled,
                isIconEnabled = boardDto.board.isIconEnabled,
                isFlagEnabled = boardDto.board.isFlagEnabled,
                isPostingEnabled = boardDto.board.isPostingEnabled,
                isLikeEnabled = boardDto.board.isLikeEnabled,
                isTagEnabled = boardDto.board.isTagEnabled,
                fileTypes = boardDto.board.fileTypes ?: emptyList(),
                maxCommentSize = boardDto.board.maxCommentSize,
                maxFileSize = boardDto.board.maxFileSize,
                tags = boardDto.board.tags,
                icons = boardDto.board.icons?.map { fromDto(it) },
            )
        )

    fun fromDto(boardShortDto: BoardShortDto): Board =
        Board(
            name = boardShortDto.name,
            id = boardShortDto.id,
            category = boardShortDto.category,
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

    fun fromDto(storedBoard: BoardCaptureSettingDto): BoardCaptchaSetting =
        BoardCaptchaSetting(
            isCaptchaEnabled = storedBoard.enabled > 0,
            captchaType =
            CaptureType
                .entries
                .firstOrNull { type ->
                    type.value in storedBoard.types.map { it.id }
                } ?: CaptureType.NoCaptcha,
        )

    private fun fromDto(iconDto: IconDto) =
        Icon(
            id = iconDto.num,
            name = iconDto.name,
            //todo собирать урл при запросе?
            url = DVACH_ROOT_URL + iconDto.url,
        )
}
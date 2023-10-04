package ru.be_more.orange_forum.data.remote.models

import ru.be_more.orange_forum.domain.model.Board

data class BoardDto(
    val board: InnerBoardDto,
    val threads: List<PostDto> = listOf()
) {
    fun toModel(boardId: String) = Board(
        name = board.name,
        id = boardId,
        category = board.category,
        threads = threads.map { it.toThread(boardId) }
    )

    data class InnerBoardDto(
        val category: String = "",
        val name: String = "",
    )
}
package ru.be_more.orange_forum.data.remote.models

import ru.be_more.orange_forum.domain.model.Board

data class BoardDto(
    val boardName : String = "",
    val category: String,
    val threads: List<PostDto> = listOf()
) {
    fun toModel(
        boardId: String,
    ) = Board(
        name = boardName,
        id = boardId,
        category = category,
        threads = threads.map { it.toThread(boardId) }
    )
}
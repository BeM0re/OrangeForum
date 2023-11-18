package ru.be_more.orange_forum.domain.model

data class Board(
    val name:String,
    val id: String,
    val category: String,
    val threads: List<BoardThread> = listOf(),
    val isFavorite: Boolean,
    val boardSetting: BoardSetting,
)
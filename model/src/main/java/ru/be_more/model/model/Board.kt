package ru.be_more.model.model

data class Board(
    val name:String,
    val id: String,
    val category: String,
    val threads: List<BoardThread> = listOf(),
    val isFavorite: Boolean,
    val boardSetting: BoardSetting,
    val imageboard: Imageboard,
)
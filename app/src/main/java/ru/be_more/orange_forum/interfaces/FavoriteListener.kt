package ru.be_more.orange_forum.interfaces

interface FavoriteListener {
    fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String)
    fun intoBoardClick(boardId: String, boardName: String)
//    fun onRemoveClick(boardId: String, threadNum: Int)
}
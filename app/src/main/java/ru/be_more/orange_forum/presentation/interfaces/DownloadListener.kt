package ru.be_more.orange_forum.presentation.interfaces

interface DownloadListener  {
    fun intoThreadClick(boardId: String, threadNum: Int, threadTitle: String)
    fun onRemoveClick(boardId: String, threadNum: Int)
}
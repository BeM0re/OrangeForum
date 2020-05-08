package ru.be_more.orange_forum.interfaces

interface BoardOnClickListener  {
    fun onThreadClick(threadNum: Int, threadTitle: String)
    fun onHideClick(threadNum: Int)
}
package ru.be_more.orange_forum.interfaces

interface BoardOnClickListener  {
    fun onIntoThreadClick(threadNum: Int, threadTitle: String)
    fun onHideClick(threadNum: Int, isHidden: Boolean)
}
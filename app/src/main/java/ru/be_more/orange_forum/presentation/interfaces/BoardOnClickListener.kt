package ru.be_more.orange_forum.presentation.interfaces

interface BoardOnClickListener  {
    fun onIntoThreadClick(threadNum: Int, threadTitle: String)
    fun onHideClick(threadNum: Int, toHide: Boolean)
}
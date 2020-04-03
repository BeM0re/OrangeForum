package ru.be_more.orange_forum.ui.board

import ru.be_more.orange_forum.model.BoardThread

interface BoardOnClickListener  {
    fun onThreadClick(thread: BoardThread)
}
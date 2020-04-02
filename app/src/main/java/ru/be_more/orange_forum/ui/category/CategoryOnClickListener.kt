package ru.be_more.orange_forum.ui.category

import ru.be_more.orange_forum.model.BoardShort

interface CategoryOnClickListener  {
    fun onBoardClick(board: BoardShort)
}
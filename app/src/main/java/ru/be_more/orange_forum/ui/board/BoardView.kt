package ru.be_more.orange_forum.ui.board

import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post

interface BoardView  {
    fun loadBoard(board: Board)
    fun setBoardMarks (isFavorite: Boolean)
    fun showPic(attachment: Attachment)
    fun showPost(post: Post)
    fun hideModal()
    fun showToast(message: String)
}
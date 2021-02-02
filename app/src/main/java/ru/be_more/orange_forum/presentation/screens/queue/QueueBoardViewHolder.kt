package ru.be_more.orange_forum.presentation.screens.queue

import android.view.View
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_board_short.*

class QueueBoardViewHolder(itemView: View?) : GroupViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    fun setBoardTitle(boardName: String) {
        tv_downloaded_board_title.text = boardName
    }

    fun setIntoBoardListener(listener: View.OnClickListener) {
        iv_board_short_to_board.setOnClickListener(listener)
    }
}
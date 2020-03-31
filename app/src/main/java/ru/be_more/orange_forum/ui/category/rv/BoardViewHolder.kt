package ru.be_more.orange_forum.ui.category.rv

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board

class BoardViewHolder(itemView: View?) : ChildViewHolder(itemView) {

    private var boardName: TextView = itemView!!.findViewById(R.id.tv_board_title)

    fun onBind(board: Board) {
        boardName.text = board.name
    }

    fun setBoardName(name: String) {
        boardName.text= name
    }
}
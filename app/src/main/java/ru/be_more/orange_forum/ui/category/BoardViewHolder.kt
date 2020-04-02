package ru.be_more.orange_forum.ui.category

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.R

class BoardViewHolder(itemView: View?) : ChildViewHolder(itemView) {

    private var boardName: TextView = itemView!!.findViewById(R.id.tv_board_title)

    fun setBoardName(name: String) {
        boardName.text= name
    }
}
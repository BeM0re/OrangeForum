package ru.be_more.orange_forum.ui.download

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category

class DownloadedBoardViewHolder(itemView: View?) : GroupViewHolder(itemView) {

private var categoryTitle: TextView = itemView!!.findViewById(R.id.tv_downloaded_board_title)

    fun setBoardTitle(boardName: String) {
        categoryTitle.text = boardName;
    }
}
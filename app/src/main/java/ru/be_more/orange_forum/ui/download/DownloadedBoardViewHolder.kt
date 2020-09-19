package ru.be_more.orange_forum.ui.download

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_downloaded_board.*
import ru.be_more.orange_forum.R

class DownloadedBoardViewHolder(itemView: View?) : GroupViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun setBoardTitle(boardName: String) {
        tv_downloaded_board_title.text = boardName
    }
}
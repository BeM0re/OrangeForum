package ru.be_more.orange_forum.ui.favorire

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_downloaded_board.*
import ru.be_more.orange_forum.R

class FavoriteBoardViewHolder(itemView: View?) : GroupViewHolder(itemView), LayoutContainer {

private var categoryTitle: TextView = itemView!!.findViewById(R.id.tv_downloaded_board_title)

    override val containerView: View?
        get() = itemView

    fun setBoardTitle(boardName: String) {
        categoryTitle.text = boardName
    }

    fun setIntoBoardListener(listener: View.OnClickListener) {
        tv_downloaded_board_title.setOnClickListener(listener)
    }
}
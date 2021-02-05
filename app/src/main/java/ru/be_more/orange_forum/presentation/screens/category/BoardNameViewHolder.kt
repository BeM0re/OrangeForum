package ru.be_more.orange_forum.presentation.screens.category

import android.view.View
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_board.*

class BoardNameViewHolder(itemView: View?) : ChildViewHolder(itemView), LayoutContainer {

    fun setBoardName(name: String) {
        tv_board_title.text= name
    }

    override val containerView: View
        get() = itemView
}
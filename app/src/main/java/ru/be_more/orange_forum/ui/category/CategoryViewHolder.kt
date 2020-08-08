package ru.be_more.orange_forum.ui.category

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Category

class CategoryViewHolder(itemView: View?) : GroupViewHolder(itemView) {

private var categoryTitle: TextView = itemView!!.findViewById(R.id.tv_category_title)

    fun setCategoryTitle(group: Category) {
        categoryTitle.text = group.title;
    }
}
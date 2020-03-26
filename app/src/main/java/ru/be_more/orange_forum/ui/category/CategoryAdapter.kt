package ru.be_more.orange_forum.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category


class CategoryAdapter(groups: List<ExpandableGroup<*>?>?) :
    ExpandableRecyclerViewAdapter<CategoryViewHolder, BoardViewHolder>(groups) {

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_board, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindChildViewHolder(
        holder: BoardViewHolder, flatPosition: Int, group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val board: Board = (group as Category).items[childIndex]!!
        holder.setBoardName(board.name.orEmpty())
    }

    override fun onBindGroupViewHolder(
        holder: CategoryViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        holder.setCategoryTitle(group as Category)
    }
}
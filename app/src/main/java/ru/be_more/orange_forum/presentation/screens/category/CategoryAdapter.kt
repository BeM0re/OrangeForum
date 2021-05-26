package ru.be_more.orange_forum.presentation.screens.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.databinding.ItemBoardBinding
import ru.be_more.orange_forum.databinding.ItemCategoryBinding
import ru.be_more.orange_forum.presentation.interfaces.CategoryOnClickListener
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Category


class CategoryAdapter(groups: List<ExpandableGroup<*>?>?, var listener: CategoryOnClickListener) :
    ExpandableRecyclerViewAdapter<CategoryViewHolder,
            BoardNameViewHolder>(groups){

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder(
            ItemCategoryBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )


    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardNameViewHolder =
        BoardNameViewHolder(
            ItemBoardBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )


    override fun onBindChildViewHolder(
        holder: BoardNameViewHolder, flatPosition: Int, group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val board: Board? = (group as Category).items[childIndex]
        holder.setBoardName(board?.name.orEmpty())
        if(board != null)
            holder.itemView.setOnClickListener { listener.onBoardClick(board.id, board.name) }
    }

    override fun onBindGroupViewHolder(
        holder: CategoryViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        holder.setCategoryTitle(group as Category)
    }
}
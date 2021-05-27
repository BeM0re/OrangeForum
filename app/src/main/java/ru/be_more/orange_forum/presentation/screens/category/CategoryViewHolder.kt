package ru.be_more.orange_forum.presentation.screens.category

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import ru.be_more.orange_forum.databinding.ItemCategoryBinding
import ru.be_more.orange_forum.domain.model.Category

class CategoryViewHolder(private val binding: ItemCategoryBinding) : GroupViewHolder(binding.root) {

    fun setCategoryTitle(group: Category) {
        binding.tvCategoryTitle.text = group.name
    }
}
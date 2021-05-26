package ru.be_more.orange_forum.presentation.screens.category

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import ru.be_more.orange_forum.databinding.ItemBoardBinding

class BoardNameViewHolder(private val binding: ItemBoardBinding) : ChildViewHolder(binding.root){

    fun setBoardName(name: String) {
        binding.tvBoardTitle.text= name
    }
}
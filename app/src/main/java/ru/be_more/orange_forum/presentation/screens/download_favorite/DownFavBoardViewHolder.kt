package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.view.View
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import ru.be_more.orange_forum.databinding.ItemBoardShortBinding

class DownFavBoardViewHolder(private val binding: ItemBoardShortBinding) : GroupViewHolder(binding.root) {

    fun setBoardTitle(boardName: String) {
        binding.tvDownloadedBoardTitle.text = boardName
    }

    fun setIntoBoardListener(listener: View.OnClickListener) {
        binding.ivBoardShortToBoard.setOnClickListener(listener)
    }


}
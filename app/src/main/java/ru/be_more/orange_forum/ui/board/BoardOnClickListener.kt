package ru.be_more.orange_forum.ui.board

import android.view.View
import android.widget.ImageView
import ru.be_more.orange_forum.model.BoardThread

interface BoardOnClickListener  {
    fun onThreadClick(thread: BoardThread)
    fun onThumbnailListener(fullPicUrl: String, duration: String? = "")
}
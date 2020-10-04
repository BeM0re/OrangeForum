package ru.be_more.orange_forum.presentation.interfaces

import android.net.Uri

interface PicOnClickListener  {
    fun onThumbnailListener(fullPicUrl: String?, duration: String? , fullPicUri: Uri?)
}
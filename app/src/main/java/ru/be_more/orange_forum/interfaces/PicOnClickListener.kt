package ru.be_more.orange_forum.interfaces

import android.net.Uri

interface PicOnClickListener  {
    fun onThumbnailListener(fullPicUrl: String, duration: String? = "")
    fun onThumbnailListener(fullPicUri: Uri, duration: String? = "")
}
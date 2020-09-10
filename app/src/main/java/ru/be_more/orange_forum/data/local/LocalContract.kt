package ru.be_more.orange_forum.data.local

import android.net.Uri

interface LocalContract {

    interface FileRepository {
        fun saveFile(url: String): Uri?
        fun removeFile(path: String)
    }

}
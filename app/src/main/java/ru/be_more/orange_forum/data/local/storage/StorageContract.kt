package ru.be_more.orange_forum.data.local.storage

import android.net.Uri

interface StorageContract {
    interface FileRepository {
        fun saveFile(url: String): Uri?
        fun removeFile(path: String)
    }

}
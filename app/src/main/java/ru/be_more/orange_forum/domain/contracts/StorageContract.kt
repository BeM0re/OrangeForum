package ru.be_more.orange_forum.domain.contracts

import android.net.Uri

interface StorageContract {
    interface LocalStorage {
        fun saveFile(url: String): Uri?
        fun delete(path: String)
    }

}
package ru.be_more.orange_forum.domain.contracts

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single

interface StorageContract {
    interface FileRepository {
        fun saveFile(url: String): Single<Uri?>
        fun removeFile(path: String): Completable
    }

}
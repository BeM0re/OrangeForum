package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.domain.RemoteContract
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.toBoard
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.domain.model.BoardThread

class BoardRepositoryImpl(
    private val dvachApi : DvachApi
) : RemoteContract.BoardRepository{

    override fun getDvachThreads(boardId: String): Single<List<BoardThread>> =
        dvachApi.getDvachThreads(boardId)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting thread error = $throwable") }
            .map { entity -> toBoard(entity) }
}
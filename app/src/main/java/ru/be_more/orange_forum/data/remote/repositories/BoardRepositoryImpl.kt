package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.toBoard
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.domain.model.BoardThread

class BoardRepositoryImpl(
    private val dvachApi : DvachApi
) : RemoteContract.BoardRepository{

    override fun getDvachThreads(boardId: String): Single<List<BoardThread>> =
        dvachApi.getDvachThreads(boardId)
            .subscribeOn(Schedulers.io())
            .map { entity -> toBoard(entity) }
            .doOnError { Log.e("M_BoardRepositoryImpl","error = $it") }
}
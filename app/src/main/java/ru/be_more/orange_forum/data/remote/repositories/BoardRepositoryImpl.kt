package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.models.RemoteConverter.Companion.toBoard
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.model.BoardThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl  @Inject constructor(
    private val dvachApi : DvachApi
) : RemoteContract.BoardRepository{

    override fun getDvachThreads(boardId: String): Observable<List<BoardThread>> =
        dvachApi.getDvachThreads(boardId)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting thread error = $throwable") }
            .map { entity -> toBoard(entity) }
}
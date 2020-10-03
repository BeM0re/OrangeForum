package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.models.DvachThread
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.findResponses
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.toPost
import ru.be_more.orange_forum.domain.converters.RemoteConverter.Companion.toThread
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.data.remote.api.DvachApi

class ThreadRepositoryImpl (
    private val dvachApi : DvachApi
) : RemoteContract.ThreadRepository{

    override fun getThread(boardId: String, threadNum: Int): Single<BoardThread> =
        dvachApi.getDvachPostsRx(boardId, threadNum, COOKIE)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "get thread via api error = $throwable") }
            .onErrorReturn { DvachThread() }
            .map { entity -> toThread(entity, threadNum)}
            .map { entity -> findResponses(entity)}

    override fun getDvachPost(
        boardId: String,
        postNum: Int,
        cookie: String
    ): Single<Post> =
        dvachApi.getDvachPostRx("get_post", boardId, postNum, COOKIE)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting post error = $throwable") }
            .map { entity -> toPost(entity[0]) }

}
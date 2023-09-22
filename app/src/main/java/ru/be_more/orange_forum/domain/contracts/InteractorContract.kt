package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.*

interface InteractorContract {

    interface CategoryInteractor {
        fun get(): Observable<List<Category>>
        fun setIsExpanded(name: String, isExpanded: Boolean): Completable
    }

    interface BoardInteractor {
        fun get(boardId: String): Observable<Board>
        fun markFavorite(boardId: String): Completable
        fun refresh(boardId: String): Completable
    }

    interface ThreadInteractor {
        fun observe(boardId: String, threadNum: Int, ): Observable<BoardThread>
        fun markFavorite(boardId: String, boardName: String, threadNum: Int, ): Completable
        fun markQueued(boardId: String, boardName: String, threadNum: Int, ): Completable
        fun markHidden(boardId: String, boardName: String, threadNum: Int, ): Completable
        fun subToUpdate(boardId: String, threadNum: Int): Completable
        fun delete(boardId: String, threadNum: Int): Completable
        @Deprecated("Maybe delete")
        fun updateNewMessages(boardId: String, threadNum: Int): Completable
        @Deprecated("Maybe delete")
        fun updateNewMessages(boardId: String, threadNum: Int, newMessageCount: Int): Completable
        @Deprecated("Maybe delete")
        fun updateLastPostNum(boardId: String, threadNum: Int, lastPostNum: Int): Completable
    }

    interface PostInteractor {
        @Deprecated("Maybe delete")
        fun getPost(boardId: String, postNum: Int): Single<Int>
    }

    interface ResponseInteractor {
        fun postResponse(
            boardId: String,
            threadNum: Int,
            comment: String,
            token:String
        ): Single<PostResponse>
    }

    interface QueueInteractor {
        fun observe(): Observable<List<Board>>
        //todo добавить какой-то запрос на обновление новых сообщений?
    }

    interface FavoriteInteractor {
        fun observe(): Observable<List<Board>>
        @Deprecated("Maybe delete")
        fun getFavoritesOnly(): Single<List<Board>>
        @Deprecated("Maybe delete")
        fun updateNewMessageCount(boardId: String, threadNum: Int, count: Int): Completable
    }

}
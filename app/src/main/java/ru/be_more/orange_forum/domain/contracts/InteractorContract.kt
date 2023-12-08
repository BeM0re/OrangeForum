package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.*

interface InteractorContract {

    interface CategoryInteractor {
        fun observe(): Observable<List<Category>>
        fun refresh(): Completable
        fun toggleExpanded(name: String): Completable
        fun search(query: String)
    }

    interface BoardInteractor {
        fun observe(boardId: String): Observable<Board>
        fun getSingle(boardId: String): Single<Board>
        fun markFavorite(boardId: String): Completable
        fun refresh(boardId: String): Completable
        fun search(query: String)
    }

    interface ThreadInteractor {
        fun refresh(boardId: String, threadNum: Int): Completable
        fun observe(boardId: String, threadNum: Int): Observable<BoardThread>
        fun subToUpdate(boardId: String, threadNum: Int): Completable
        fun save(boardId: String, threadNum: Int): Completable
        fun markFavorite(boardId: String, threadNum: Int): Completable
        fun markQueued(boardId: String, threadNum: Int): Completable
        fun markHidden(boardId: String, threadNum: Int): Completable
        fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int): Completable
        fun delete(boardId: String, threadNum: Int): Completable
    }

    interface PostInteractor {
        fun getPost(
            boardId: String,
            threadNum: Int,
            postNum: Int,
        ): Single<Post>
    }

    interface ReplyInteractor {
        fun getCaptcha(boardId: String, threadNum: Int?): Single<String>
        fun reply(
            boardId: String,
            threadNum: Int,
            comment: String,
            isOp: Boolean,
            subject: String,
            email: String,
            name: String,
            tag: String,
            captchaSolvedString: String?,
        ): Completable
        fun createThread(
            boardId: String,
            comment: String,
            isOp: Boolean,
            subject: String,
            email: String,
            name: String,
            tag: String,
            captchaSolvedString: String?,
        ): Completable
    }

    interface QueueInteractor {
        fun observe(): Observable<List<Board>>
        fun clear(): Completable
    }

    interface FavoriteInteractor {
        fun observe(): Observable<List<Board>>
        fun observeNewMessages(): Observable<Boolean>
        fun updatingFavoritesSubscription(): Completable
        fun updateFavoriteThreadInfo(): Completable

    }

}
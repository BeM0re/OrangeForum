package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.*

interface InteractorContract {

    interface CategoryInteractor {
        fun getCategories(): Single<List<Category>>
    }

    interface BoardInteractor {
        fun getBoard(boardId: String, boardName: String): Single<Board>
        fun markBoardFavorite(boardId: String, boardName: String): Completable
        fun unmarkBoardFavorite(boardId: String): Completable
    }

    interface ThreadInteractor {
        fun getThread(
            boardId: String,
            threadNum: Int,
            forceUpdate: Boolean): Single<BoardThread>

        fun markThreadFavorite(
            boardId: String,
            boardName: String,
            threadNum: Int,
            isFavorite: Boolean): Completable

        fun markThreadQueued(
            boardId: String,
            boardName: String,
            threadNum: Int,
            isQueued: Boolean): Completable

        fun downloadThread(
            boardId: String,
            boardName: String,
            threadNum: Int):Completable

        fun deleteThread(boardId: String, threadNum: Int): Completable

        fun markThreadHidden(
            boardId: String,
            boardName: String,
            threadNum: Int,
            isHidden: Boolean): Completable
    }

    interface PostInteractor {
        fun getPost(boardId: String, postNum: Int): Single<Int>
    }

    interface ResponseInteractor {
        fun postResponse(boardId: String,
                         threadNum: Int,
                         comment: String,
                         token:String): Single<PostResponse>
    }

    interface QueueInteractor {
        fun getQueue(): Single<List<Board>>
    }

    interface DownFavInteractor {
        fun getDownFavs(): Single<List<Board>>
        fun getFavoritesOnly(): Single<List<Board>>
    }

}
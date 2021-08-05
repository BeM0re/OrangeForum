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
        fun getThread(boardId: String, threadNum: Int, forceUpdate: Boolean): Single<BoardThread>
        fun addThreadToFavorite(threadNum: Int, boardId: String, boardName: String): Completable
        fun addThreadToQueue(threadNum: Int, boardId: String, boardName: String): Completable
        fun removeThreadFromFavorite(boardId: String, threadNum: Int):Completable
        fun removeThreadFromQueue(boardId: String, threadNum: Int):Completable
        fun downloadThread(threadNum: Int, boardId: String, boardName: String):Completable
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun hideThread(boardId: String, boardName: String, threadNum: Int): Completable
        fun unhideThread(boardId: String, threadNum: Int):Completable
    }

    interface PostInteractor {
        fun getPost(boardId: String, postNum: Int): Single<Post>
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
        fun getDownloads(): Single<List<Board>>
    }

}
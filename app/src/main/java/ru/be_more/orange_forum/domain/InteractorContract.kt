package ru.be_more.orange_forum.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.data.remote.models.DvachPostResponse
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post

interface InteractorContract {

    interface CategoryInteractor {
        fun getCategories(): Single<List<Category>>
    }

    interface BoardInteractor {
        fun getBoard(boardId: String): Single<Board>
        fun markBoardFavorite(boardId: String, boardName: String): Single<Int>
        fun unmarkBoardFavorite(boardId: String): Single<Unit>
    }

    interface ThreadInteractor {
        fun getThread(boardId: String, threadNum: Int): Single<BoardThread>
        fun markThreadFavorite(thread: BoardThread, boardId: String, boardName: String): Completable
        fun unmarkThreadFavorite(boardId: String, threadNum: Int):Completable
        fun downloadThread(thread: BoardThread, boardId: String, boardName: String):Completable
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<BoardThread?>
        fun markThreadHidden(boardId: String, boardName: String, threadNum: Int):Completable
        fun unmarkThreadHidden(boardId: String, threadNum: Int):Completable
    }

    interface PostInteractor {
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
        fun getPost(boardId: String, postNum: Int): Single<Post>
    }

    interface ResponseInteractor {
        fun postResponse(boardId: String,
                         threadNum: Int,
                         comment: String,
                         token:String): Single<DvachPostResponse>
    }

    interface FavoriteInteractor {
        fun getFavorites(): Single<List<Board>>
    }

    interface DownloadInteractor {
        fun getDownloads(): Single<List<Board>>
    }

}
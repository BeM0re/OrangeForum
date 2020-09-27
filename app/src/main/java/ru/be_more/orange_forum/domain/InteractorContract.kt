package ru.be_more.orange_forum.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.data.remote.models.DvachPostResponse
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post

interface InteractorContract {

    interface BaseInteractor {
        fun release()
    }

    interface CategoryInteractor: BaseInteractor {
        fun getCategories(): Single<List<Category>>
    }

    interface BoardInteractor: BaseInteractor {
        fun getBoard(boardId: String, boardName: String): Single<Board>
        fun markBoardFavorite(boardId: String, boardName: String): Completable
        fun unmarkBoardFavorite(boardId: String): Completable
    }

    interface ThreadInteractor: BaseInteractor {
        fun getThread(boardId: String, threadNum: Int): Single<BoardThread>
        fun markThreadFavorite(threadNum: Int, boardId: String, boardName: String): Completable
        fun unmarkThreadFavorite(boardId: String, threadNum: Int):Completable
        fun downloadThread(threadNum: Int, boardId: String, boardName: String):Completable
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<BoardThread?>
        fun markThreadHidden(boardId: String, boardName: String, threadNum: Int): Completable
        fun unmarkThreadHidden(boardId: String, threadNum: Int):Completable
    }

    interface PostInteractor: BaseInteractor {
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
        fun getPost(boardId: String, postNum: Int): Single<Post>
    }

    interface ResponseInteractor: BaseInteractor {
        fun postResponse(boardId: String,
                         threadNum: Int,
                         comment: String,
                         token:String): Single<DvachPostResponse>
    }

    interface FavoriteInteractor: BaseInteractor {
        fun getFavorites(): Single<List<Board>>
    }

    interface DownloadInteractor: BaseInteractor {
        fun getDownloads(): Single<List<Board>>
    }

}
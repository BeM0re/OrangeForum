package ru.be_more.orange_forum.data.local

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.jsoup.Connection
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.repositories.DvachDbRepository

interface LocalContract {

    interface BaseRepository{
        fun release()
    }

    interface BoardRepository: BaseRepository{
        fun getBoard(boardId: String): Single<Board>
        fun markBoardFavorite(boardId: String, boardName: String): Single<Int>
        fun unmarkBoardFavorite(boardId: String): Single<Unit>
    }

    interface ThreadRepository{
        fun downloadThread(thread: BoardThread, boardId: String): Single<Unit>
        fun deleteThread(boardId: String, threadNum: Int): Single<Unit>
//        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<BoardThread?>
        fun markThreadFavorite(thread: BoardThread, boardId: String, boardName: String): Single<Unit>
        fun unmarkThreadFavorite(boardId: String, threadNum: Int): Single<Unit>
        fun markThreadHidden(boardId: String, threadNum: Int): Single<List<StoredThread>>
        fun unmarkThreadHidden(boardId: String, threadNum: Int): Single<Unit>
    }

    interface PostRepository{
        fun savePost(post: Post,
                     threadNum: Int,
                     boardId: String,
                     localPath: String,
                     localThumbnail: String): Single<Unit>
        fun getPost(boardId: String, postNum: Int): Single<Post>
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
    }

    interface FavoriteRepository{
        fun getFavorites(): Single<List<Board>>
    }

    interface DownloadRepository{
        fun getDownloads(): Single<List<Board>>
    }
}
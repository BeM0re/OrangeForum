package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

interface DbContract {

    interface BoardRepository{
        fun getBoard(boardId: String, boardName: String): Single<Board>
        fun getBoardCount(boardId: String): Single<Int>
        fun insertBoard(boardId: String, boardName: String, isFavorite: Boolean)
        fun markBoardFavorite(boardId: String, boardName: String)
        fun unmarkBoardFavorite(boardId: String)
    }

    interface ThreadRepository{
        fun insertThread(thread: BoardThread, boardId: String)
        /**@return true if new thread inserted, false if thread is existed and not inserted*/
        fun insertThreadSafety(thread: BoardThread, boardId: String): Single<Boolean>
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>>
        fun addThreadToQueue( boardId: String, threadNum: Int)
        fun addThreadToFavorite(boardId: String, threadNum: Int)
        fun removeThreadFromFavorite(boardId: String, threadNum: Int)
        fun removeThreadFromQueue(boardId: String, threadNum: Int)
        fun hideThread(boardId: String, threadNum: Int)
        fun unhideThread(boardId: String, threadNum: Int)
    }

    interface PostRepository{
        fun savePost(post: Post, threadNum: Int, boardId: String)
        fun savePosts(posts: List<Post>, threadNum: Int, boardId: String)
        fun getPost(boardId: String, postNum: Int): Single<Post>
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
    }

    interface FileRepository{
        fun saveFile(file: AttachFile,
                     postNum: Int,
                     threadNum: Int,
                     boardId: String)
        fun saveFiles(files: List<AttachFile>,
                     postNum: Int,
                     threadNum: Int,
                     boardId: String)
        fun getPostFiles(boardId: String, postNum: Int): Single<List<AttachFile>>
    }

    interface QueueRepository{
        fun getQueue(): Single<List<Board>>
    }

    interface DownFavRepository{
        fun getDownloadsAndFavorites(): Single<List<Board>>
    }
}
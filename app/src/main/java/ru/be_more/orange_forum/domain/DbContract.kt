package ru.be_more.orange_forum.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

interface DbContract {

    interface BoardRepository{
        fun getBoards(): Single<List<Board>>
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
        fun deleteThread(boardId: String, threadNum: Int)
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>>
        fun getDownloadedThreads(): Single<List<Pair<BoardThread, String>>>
        fun markThreadFavorite( boardId: String, threadNum: Int)
        fun unmarkThreadFavorite(boardId: String, threadNum: Int)
        fun markThreadHidden(boardId: String, threadNum: Int)
        fun unmarkThreadHidden(boardId: String, threadNum: Int)
    }

    interface PostRepository{
        fun savePost(post: Post, threadNum: Int, boardId: String)
        fun savePosts(posts: List<Post>, threadNum: Int, boardId: String)
        fun getPost(boardId: String, postNum: Int): Single<Post>
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
        fun getOpPosts(): Single<List<Pair<Post, Int>>>
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
        fun getThreadFiles(boardId: String, threadNum: Int): Single<List<Pair<AttachFile, Int>>>
        fun deleteThreadFiles(boardId: String, threadNum: Int)
        fun deletePostFiles(boardId: String, postNum: Int)
        fun getOpFiles(): Single<List<Pair<AttachFile, Int>>>
    }

    interface FavoriteRepository{
        fun getFavorites(): Single<List<Board>>
    }

    interface DownloadRepository{
        fun getDownloads(): Single<List<Board>>
    }
}
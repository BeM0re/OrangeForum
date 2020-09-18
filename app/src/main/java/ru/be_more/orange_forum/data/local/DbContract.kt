package ru.be_more.orange_forum.data.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

interface DbContract {

    interface BaseRepository{
        fun release()
    }

    interface BoardRepository: BaseRepository{
        fun getBoards(): Single<List<Board>>
        fun getBoard(boardId: String): Single<Board>
        fun getBoardCount(boardId: String): Single<Int>
        fun insertBoard(boardId: String, boardName: String): Single<List<StoredBoard>>
        fun markBoardFavorite(boardId: String, boardName: String): Single<Int>
        fun unmarkBoardFavorite(boardId: String): Single<Unit>
    }

    interface ThreadRepository: BaseRepository{
        /**@return true if new thread inserted, false if thread is existed and not inserted*/
        fun insertThread(thread: BoardThread, boardId: String): Single<Boolean>
        fun downloadThread(thread: BoardThread, boardId: String): Completable
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>>
        fun getDownloadedThreads(): Single<List<Pair<BoardThread, String>>>
        fun markThreadFavorite(thread: BoardThread, boardId: String, boardName: String): Completable
        fun markThreadFavorite( boardId: String, threadNum: Int)
        fun unmarkThreadFavorite(boardId: String, threadNum: Int): Completable
        fun markThreadHidden(boardId: String, threadNum: Int): Completable
        fun unmarkThreadHidden(boardId: String, threadNum: Int): Completable
    }

    interface PostRepository: BaseRepository{
        fun savePost(post: Post,
                     threadNum: Int,
                     boardId: String)
        fun getPost(boardId: String, postNum: Int): Single<Post>
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
        fun getOpPosts(): Single<List<Pair<Post, Int>>>
    }

    interface FileRepository: BaseRepository{
        fun saveFile(file: AttachFile,
                     postNum: Int,
                     threadNum: Int,
                     boardId: String): Completable
        fun saveFiles(files: List<AttachFile>,
                     postNum: Int,
                     threadNum: Int,
                     boardId: String)
//        fun getFile(boardId: String, postNum: Int): Single<AttachFile>
        fun getPostFiles(boardId: String, postNum: Int): Single<List<AttachFile>>
        fun getThreadFiles(boardId: String, threadNum: Int): Single<List<Pair<AttachFile, Int>>>
        fun deleteThreadFiles(boardId: String, threadNum: Int): Completable
        fun deletePostFiles(boardId: String, postNum: Int): Completable
        fun getOpFiles(): Single<List<Pair<AttachFile, Int>>>
    }


    interface FavoriteRepository: BaseRepository{
        fun getFavorites(): Single<List<Board>>
    }

    interface DownloadRepository: BaseRepository{
        fun getDownloads(): Single<List<Board>>
    }
}
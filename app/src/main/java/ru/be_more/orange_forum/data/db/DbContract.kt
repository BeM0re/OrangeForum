package ru.be_more.orange_forum.data.db

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.data.db.db.entities.StoredThread
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
        fun insertBoard(boardId: String, boardName: String)
        fun markBoardFavorite(boardId: String, boardName: String): Single<Int>
        fun unmarkBoardFavorite(boardId: String): Single<Unit>
    }

    interface ThreadRepository{
        fun insertThread(thread: BoardThread, boardId: String)
        fun downloadThread(thread: BoardThread, boardId: String): Completable
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>>
        fun getDownloadedThreads(): Single<List<Pair<BoardThread, String>>>
        fun markThreadFavorite(thread: BoardThread, boardId: String, boardName: String): Completable
        fun unmarkThreadFavorite(boardId: String, threadNum: Int): Completable
        fun markThreadHidden(boardId: String, threadNum: Int): Completable
        fun unmarkThreadHidden(boardId: String, threadNum: Int): Completable
    }

    interface PostRepository{
        fun savePost(post: Post,
                     threadNum: Int,
                     boardId: String): Single<Unit>
        fun getPost(boardId: String, postNum: Int): Single<Post>
        fun getPosts(boardId: String, threadNum: Int): Single<List<Post>>
        fun getOpPosts(): Single<List<Pair<Post, Int>>>
    }

    interface FileRepository{
        fun saveFile(file: AttachFile,
                     postNum: Int,
                     threadNum: Int,
                     boardId: String): Completable
//        fun getFile(boardId: String, postNum: Int): Single<AttachFile>
        fun getPostFiles(boardId: String, postNum: Int): Single<List<AttachFile>>
        fun getThreadFiles(boardId: String, threadNum: Int): Single<List<Pair<AttachFile, Int>>>
        fun deleteThreadFiles(boardId: String, threadNum: Int): Completable
        fun deletePostFiles(boardId: String, postNum: Int): Completable
        fun getOpFiles(): Single<List<Pair<AttachFile, Int>>>
    }


    interface FavoriteRepository{
        fun getFavorites(): Single<List<Board>>
    }

    interface DownloadRepository{
        fun getDownloads(): Single<List<Board>>
    }
}
package ru.be_more.orange_forum.data.local

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.repositories.DvachDbRepository

interface LocalContract {

    interface BoardRepository{
        fun getBoard(boardId: String): Observable<Board>
        fun markBoardFavorite(boardId: String, boardName: String): Disposable
        fun unmarkBoardFavorite(boardId: String): Disposable
    }

    interface ThreadRepository{
        fun downloadThread(thread: BoardThread, boardId: String, boardName: String): Disposable
        fun deleteThread(boardId: String, threadNum: Int): Disposable
        fun getThreadOrEmpty(boardId: String, threadNum: Int): Observable<BoardThread?>
        fun markThreadFavorite(thread: BoardThread, boardId: String, boardName: String): Disposable
        fun unmarkThreadFavorite(boardId: String, threadNum: Int): Disposable
        fun markThreadHidden(boardId: String, threadNum: Int): Disposable
        fun unmarkThreadHidden(boardId: String, threadNum: Int): Disposable
    }

    interface PostRepository{
        fun savePost(post: Post, threadNum: Int, boardId: String)
        fun getPost(boardId: String, postNum: Int): Observable<Post>
        fun getPosts(boardId: String, threadNum: Int): Observable<List<Post>>
    }

    interface FavoriteRepository{
        fun getFavorites(): Observable<List<Board>>
    }

    interface DownloadRepository{
        fun getDownloads(): Observable<List<Board>>
    }
}
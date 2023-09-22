package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post

interface DbContract {

    interface CategoryRepository {
        fun insert(categories: List<Category>): Completable
        fun observe(): Observable<List<Category>>
        fun get(name: String): Single<Category>
        fun setIsExpanded(name: String, isExpanded: Boolean): Completable
    }

    interface BoardRepository {
        fun get(boardId: String): Single<Board>
        fun observe(boardId: String): Observable<Board>
        fun observeList(): Observable<List<Board>>
        @Deprecated("why boardId? remove?")
        fun observeCount(boardId: String): Observable<Int>
        fun insert(board: Board): Completable
        @Deprecated("Delete")
        fun insert(boardId: String, boardName: String, isFavorite: Boolean): Completable
        fun markFavorite(boardId: String, isFavorite: Boolean): Completable
        @Deprecated("Delete")
        fun updateThreadNewMessageCounter(boardId: String, threadNum: Int, count: Int):Completable
    }

    interface ThreadRepository {
        /**Save thread w/o pictures*/
        fun insert(thread: BoardThread): Completable
        /**Save thread w/o pictures*/
        fun insert(threads: List<BoardThread>): Completable
        /**Save thread with pictures*/
        fun save(thread: BoardThread, boardId: String): Completable
        fun observe(boardId: String, threadNum: Int): Observable<BoardThread>
        fun get(boardId: String, threadNum: Int): Maybe<BoardThread>
        fun observeList(boardId: String): Observable<List<BoardThread>>
        fun observeFavorite(): Observable<List<BoardThread>>
        fun observeQueued(): Observable<List<BoardThread>>
        fun delete(boardId: String, threadNum: Int): Completable
        fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int): Completable
        fun markFavorite(boardId: String, threadNum: Int, isFavorite: Boolean): Completable
        fun markHidden(boardId: String, threadNum: Int, isHidden: Boolean): Completable
        fun markQueued(boardId: String, threadNum: Int, isQueued: Boolean): Completable
        fun delete(boardId: String): Completable
    }

    interface PostRepository {
        fun insert(post: Post): Completable
        fun insert(posts: List<Post>): Completable
        fun insertOp(posts: List<Post>): Completable
        fun observeOp(boardId: String): Observable<List<Post>>
        fun observe(boardId: String, threadNum: Int): Observable<List<Post>>
        fun get(boardId: String, threadNum: Int): Single<List<Post>>
        fun delete(boardId: String, threadNum: Int): Completable
    }
}
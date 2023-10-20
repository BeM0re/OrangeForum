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
        /**empty = empty board list*/
        fun getEmpty(name: String): Single<Category>
        fun setIsExpanded(name: String, isExpanded: Boolean): Completable
        fun delete(): Completable
    }

    interface BoardRepository {
        fun get(boardId: String): Single<Board>
        fun observe(boardId: String): Observable<Board>
        fun observeList(): Observable<List<Board>>
        fun insertKeepingState(board: Board): Completable
        fun insertKeepingState(boards: List<Board>): Completable
        fun markFavorite(boardId: String, isFavorite: Boolean): Completable
        @Deprecated("Delete")
        fun updateThreadNewMessageCounter(boardId: String, threadNum: Int, count: Int):Completable
        fun deleteKeepingState(): Completable
    }

    interface ThreadRepository {
        /**Save thread w/o pictures*/
        fun insert(thread: BoardThread): Completable
        /**Save thread w/o pictures*/
        fun insertKeepingState(threads: List<BoardThread>): Completable
        /**Save thread with pictures*/
        fun save(thread: BoardThread, boardId: String): Completable
        fun observe(boardId: String, threadNum: Int): Observable<BoardThread>
        fun get(boardId: String, threadNum: Int): Maybe<BoardThread>
        fun observeList(boardId: String): Observable<List<BoardThread>>
        fun observeFavorite(): Observable<List<BoardThread>>
        fun observeQueued(): Observable<List<BoardThread>>
        fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int): Completable
        fun markFavorite(boardId: String, threadNum: Int, isFavorite: Boolean): Completable
        fun markHidden(boardId: String, threadNum: Int, isHidden: Boolean): Completable
        fun markQueued(boardId: String, threadNum: Int, isQueued: Boolean): Completable
        fun markQueuedAll(isQueued: Boolean): Completable
        fun delete(boardId: String, threadNum: Int): Completable
        fun deleteKeepingState(boardId: String): Completable
        fun deleteExceptGiven(boardId: String, liveThreadNumList: List<Int>): Completable
    }

    interface PostRepository {
        fun insert(post: Post): Completable
        fun insert(posts: List<Post>): Completable
        fun insertMissing(thread: BoardThread): Completable
        /** save = insert + save images*/
        fun save(posts: List<Post>): Completable
        fun insertOp(posts: List<Post>): Completable
        fun observeOp(boardId: String): Observable<List<Post>>
        fun observe(boardId: String, threadNum: Int): Observable<List<Post>>
        fun get(boardId: String, post: Int): Maybe<Post>
        fun getThreadPosts(boardId: String, threadNum: Int): Single<List<Post>>
        fun delete(boardId: String, threadNum: Int): Completable
    }
}
package ru.be_more.orange_forum.domain.contracts

import kotlinx.coroutines.flow.Flow
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.domain.model.Post

interface DbContract {

    interface CategoryRepository {
        suspend fun insert(categories: List<Category>)
        fun getFlowList(): Flow<List<Category>>
        /**empty = empty board list*/
        suspend fun getEmpty(name: String): Category
        suspend fun setIsExpanded(name: String, isExpanded: Boolean)
        suspend fun delete()
    }

    interface BoardRepository {
        suspend fun get(boardId: String): Board?
        fun getFlow(boardId: String): Flow<Board>
        fun getListFlow(): Flow<List<Board>>
        suspend fun insertKeepingState(board: Board)
        suspend fun insertKeepingState(boards: List<Board>)
        suspend fun markFavorite(boardId: String, isFavorite: Boolean)
        @Deprecated("Delete")
        suspend fun updateThreadNewMessageCounter(boardId: String, threadNum: Int, count: Int)
        suspend fun deleteKeepingState()
    }

    interface ThreadRepository {
        /**Save thread w/o pictures*/
        suspend fun insert(thread: BoardThread)
        /**Save thread w/o pictures*/
        suspend fun insertKeepingState(threads: List<BoardThread>)
        /**Save thread with pictures*/
        suspend fun save(thread: BoardThread, boardId: String)
         fun getFlow(boardId: String, threadNum: Int): Flow<BoardThread>
        suspend fun get(boardId: String, threadNum: Int): BoardThread?
        suspend fun getFavorites(): List<BoardThread>
        suspend fun getQueued(): List<BoardThread>
        fun getListFlow(boardId: String): Flow<List<BoardThread>>
        fun getFavoriteFlow(): Flow<List<BoardThread>>
        fun getQueuedFlow(): Flow<List<BoardThread>>
        suspend fun setPostCount(boardId: String, threadNum: Int, postNum: Int)
        suspend fun setLasthit(boardId: String, threadNum: Int, lasthit: Long)
        suspend fun setHasNewPost(boardId: String, threadNum: Int, hasNewPost: Boolean)
        suspend fun setIsDrown(boardId: String, threadNum: Int, isDrown: Boolean)
        suspend fun markFavorite(boardId: String, threadNum: Int, isFavorite: Boolean)
        suspend fun markHidden(boardId: String, threadNum: Int, isHidden: Boolean)
        suspend fun markQueued(boardId: String, threadNum: Int, isQueued: Boolean)
        suspend fun markQueuedAll(isQueued: Boolean)
        suspend fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int)
        suspend fun delete(boardId: String, threadNum: Int)
        suspend fun deleteKeepingState(boardId: String)
        suspend fun deleteExceptGiven(boardId: String, liveThreadNumList: List<Int>)
    }

    interface PostRepository {
        suspend fun insert(post: Post)
        suspend fun insert(posts: List<Post>)
        suspend fun insertMissing(thread: BoardThread)
        /** save = insert + save images*/
        suspend fun save(posts: List<Post>)
        suspend fun insertOp(posts: List<Post>)
        fun getOpListFlow(boardId: String): Flow<List<Post>>
        fun getListFlow(boardId: String, threadNum: Int): Flow<List<Post>>
        suspend fun get(boardId: String, post: Int): Post?
        suspend fun getThreadPosts(boardId: String, threadNum: Int): List<Post>
        suspend fun delete(boardId: String, threadNum: Int)
    }
}
package ru.be_more.orange_forum.domain.contracts

import kotlinx.coroutines.flow.Flow
import ru.be_more.orange_forum.domain.model.*

interface InteractorContract {

    interface CategoryInteractor {
        fun getCategoryListFlow(): Flow<List<Category>>
        suspend fun refresh()
        suspend fun toggleExpanded(name: String)
        suspend fun search(query: String)
    }

    interface BoardInteractor {
        fun getBoardFlow(boardId: String): Flow<Board>
        suspend fun getBoard(boardId: String): Board?
        suspend fun markFavorite(boardId: String)
        suspend fun refresh(boardId: String)
        suspend fun search(query: String)
    }

    interface ThreadInteractor {
        suspend fun refresh(boardId: String, threadNum: Int)
        fun getBoardFlow(boardId: String, threadNum: Int): Flow<BoardThread>
        suspend fun subToUpdate(boardId: String, threadNum: Int)
        suspend fun save(boardId: String, threadNum: Int)
        suspend fun markFavorite(boardId: String, threadNum: Int)
        suspend fun markQueued(boardId: String, threadNum: Int)
        suspend fun markHidden(boardId: String, threadNum: Int)
        suspend fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int)
        suspend fun delete(boardId: String, threadNum: Int)
    }

    interface PostInteractor {
        suspend fun getPost(
            boardId: String,
            threadNum: Int,
            postNum: Int,
        ): Post
    }

    interface ReplyInteractor {
        suspend fun getCaptcha(boardId: String, threadNum: Int?): String
        suspend fun reply(
            boardId: String,
            threadNum: Int,
            comment: String,
            isOp: Boolean,
            subject: String,
            email: String,
            name: String,
            tag: String,
            captchaSolvedString: String?,
        )
        suspend fun createThread(
            boardId: String,
            comment: String,
            isOp: Boolean,
            subject: String,
            email: String,
            name: String,
            tag: String,
            captchaSolvedString: String?,
        )
    }

    interface QueueInteractor {
        fun getBoardListFlow(): Flow<List<Board>>
        suspend fun clear()
    }

    interface FavoriteInteractor {
        fun getBoardListFlow(): Flow<List<Board>>
        fun getBoardFlow(): Flow<Boolean>
        suspend fun updatingFavoritesSubscription()
        suspend fun updateFavoriteThreadInfo()

    }

}
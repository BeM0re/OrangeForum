package ru.be_more.orange_forum.domain.contracts

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

interface DbContract {

    interface BoardRepository{
        fun getBoard(boardId: String): Maybe<Board>
        fun getBoards(): Single<List<Board>>
        fun getBoardsObservable(): Observable<List<Board>>
        fun getBoardCount(boardId: String): Single<Int>
        fun insertBoard(board: Board)
        fun insertBoard(boardId: String, boardName: String, isFavorite: Boolean)
        fun markBoardFavorite(boardId: String, boardName: String)
        fun unmarkBoardFavorite(boardId: String)
        fun insertThreadIntoBoard(
            boardId: String,
            boardName: String,
            thread: BoardThread
        ):Completable
        fun updateThreadNewMessageCounter(
            boardId: String,
            threadNum: Int,
            count: Int
        ):Completable
    }

    interface ThreadRepository{
        /**Save thread w/o pictures*/
        fun insertThread(thread: BoardThread, boardId: String)
        /**Save thread with pictures*/
        fun saveThread(thread: BoardThread, boardId: String)
        fun getThread(boardId: String, threadNum: Int): Maybe<BoardThread>
        fun deleteThread(boardId: String, threadNum: Int): Completable
        fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int)
    }
}
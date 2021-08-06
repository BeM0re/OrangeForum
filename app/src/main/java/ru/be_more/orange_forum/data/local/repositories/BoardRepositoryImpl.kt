package ru.be_more.orange_forum.data.local.repositories

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredThread
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread

class BoardRepositoryImpl (
    private val dao: DvachDao
) : DbContract.BoardRepository{

    override fun getBoard(boardId: String): Maybe<Board> =
        dao.getBoard(boardId)
            .map { toModelBoard(it) }
            .doOnError { Log.e("M_BoardRepositoryImpl","error = $it") }

    override fun getBoards(): Single<List<Board>> {
        return dao.getBoards()
            .map { boardList ->
                boardList.map { toModelBoard(it) }
            }
    }

    override fun getBoardsObservable(): Observable<List<Board>> {
        return dao.getBoardsObservable()
            .map { boardList ->
                boardList.map { toModelBoard(it) }
            }
    }

    override fun getBoardCount(boardId: String): Single<Int> =
        dao.getBoardCount(boardId)

    override fun insertBoard(board: Board) =
        dao.insertBoard(
            StoredBoard(
                id = board.id,
                categoryId = "",
                name = board.name,
                threads = board.threads.map { toStoredThread(it, board.id) },
                isFavorite = board.isFavorite))

    override fun insertBoard(boardId: String, boardName: String, isFavorite: Boolean) =
        dao.insertBoard(
            StoredBoard(
                id = boardId,
                categoryId = "",
                name = boardName,
                threads = listOf(),
                isFavorite = isFavorite))

    override fun markBoardFavorite(boardId: String, boardName: String) =
        dao.markBoardFavorite(boardId)

    override fun unmarkBoardFavorite(boardId: String) =
        dao.unmarkBoardFavorite(boardId)

    override fun insertThreadIntoBoard(boardId: String, boardName: String, thread: BoardThread): Completable {
        return dao.getBoard(boardId)
            .switchIfEmpty(Single.just(StoredBoard(id = boardId, name = boardName)))
            .doOnSuccess { board ->
                val index = board.threads.indexOfFirst { it.num == thread.num }
                dao.insertBoard(
                    if (index == -1)
                        board.copy(threads = board.threads.plus(toStoredThread(thread, boardId)))
                    else {
                        board.copy(
                            threads = board.threads.toMutableList()
                                .apply { this[index] = toStoredThread(thread, boardId) }
                        )
                    }
                )
            }
            .ignoreElement()
    }


}
package ru.be_more.orange_forum.data.local.repositories

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.domain.model.Board

class BoardRepositoryImpl(
    private val dao: BoardDao
) : DbContract.BoardRepository {

    override fun get(boardId: String): Single<Board> =
        dao.get(boardId)
            .switchIfEmpty (
                Single.error(Throwable("No board found"))
            )
            .map { it.toModel() }

    override fun observe(boardId: String): Observable<Board> =
        dao.observe(boardId)
            .map { it.toModel() }
            .doOnError { Log.e("BoardRepositoryImpl","error = $it") }

    override fun observeList(): Observable<List<Board>> =
        dao.observeList()
            .map { boardList ->
                boardList.map { it.toModel() }
            }

    @Deprecated("why boardId? remove?")
    override fun observeCount(boardId: String): Observable<Int> =
        dao.observeCount(boardId)

    override fun delete(boardId: String): Completable =
        dao.delete(boardId)

    fun getBoardsObservable(): Observable<List<Board>> =
        dao.observeList()
            .map { boardList ->
                boardList.map { it.toModel() }
            }

    override fun insert(board: Board): Completable =
        dao.get(board.id)
            .map { it.isFavorite }
            .defaultIfEmpty(false)
            .flatMapCompletable { isFavorite ->
                dao.insertBoard(
                    StoredBoard(
                        board.copy(isFavorite = isFavorite)
                    )
                )
            }

    override fun insert(boards: List<Board>): Completable =
        dao.getFavorites()
            .flatMapCompletable { favorites ->
                dao.insertBoardList(
                    boards.map { board ->
                        StoredBoard(
                            board.copy(isFavorite = board.id in favorites)
                        )
                    }
                )
            }

    @Deprecated("Delete")
    override fun insert(boardId: String, boardName: String, isFavorite: Boolean) =
        dao.insertBoard(
            StoredBoard(
                id = boardId,
                category = "",
                name = boardName,
                isFavorite = isFavorite)
        )

    override fun markFavorite(
        boardId: String,
        isFavorite: Boolean
    ): Completable =
        dao.markFavorite(boardId, isFavorite)

    @Deprecated("Delete")
    override fun updateThreadNewMessageCounter(
        boardId: String,
        threadNum: Int,
        count: Int
    ): Completable =
        Completable.complete()
        /* dao.get(boardId)
            .map { board ->
                val index = board.threads.indexOfFirst{ it.num == threadNum}
                if (index > -1)
                    board.copy(
                        threads = board.threads.toMutableList().apply {
                            set(index, get(index).copy(newMessageAmount = count))
                        }
                    )
                else
                    board
            }
            .doOnSuccess { dao.insertBoard(it) }
            .ignoreElement()*/

}
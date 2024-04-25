package ru.be_more.orange_forum.data.local.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.domain.model.Board

class BoardRepositoryImpl(
    private val dao: BoardDao
) : DbContract.BoardRepository {

    override suspend fun get(boardId: String): Board? =
        dao.get(boardId)
            ?.toModel()

    override fun getFlow(boardId: String): Flow<Board> =
        dao.getFlow(boardId)
            .map { it.toModel() }

    override fun getListFlow(): Flow<List<Board>> =
        dao.getListFlow()
            .map { boardList ->
                boardList.map { it.toModel() }
            }

    override suspend fun insertKeepingState(board: Board) =
        (dao.get(board.id)
            ?.isFavorite ?: false)
            .let { isFavorite ->
                dao.insertBoard(
                    StoredBoard(
                        board.copy(isFavorite = isFavorite)
                    )
                )
            }

    override suspend fun insertKeepingState(boards: List<Board>) =
        dao.getFavorites()
            .let { favorites ->
                dao.insertBoardList(
                    boards.map { board ->
                        StoredBoard(
                            board.copy(isFavorite = board.id in favorites)
                        )
                    }
                )
            }

    override suspend fun markFavorite(boardId: String, isFavorite: Boolean) =
        dao.markFavorite(boardId, isFavorite)

    @Deprecated("Delete")
    override suspend fun updateThreadNewMessageCounter(
        boardId: String,
        threadNum: Int,
        count: Int
    ): Unit { }
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


    override suspend fun deleteKeepingState() =
        dao.deleteKeepingState()
}
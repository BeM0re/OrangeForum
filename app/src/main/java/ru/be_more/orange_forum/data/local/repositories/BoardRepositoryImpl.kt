package ru.be_more.orange_forum.data.local.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.database.db.dao.BoardDao
import ru.be_more.database.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.dbConverters.BoardFactory
import ru.be_more.model.model.Board
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val dao: BoardDao
) : DbContract.BoardRepository {

    override suspend fun get(boardId: String): Board? =
        dao.get(boardId)
            ?.let { BoardFactory.fromEntity(it) }

    override fun getFlow(boardId: String): Flow<Board> =
        dao.getFlow(boardId)
            .map { BoardFactory.fromEntity(it) }

    override fun getListFlow(): Flow<List<Board>> =
        dao.getListFlow()
            .map { boardList ->
                boardList.map { BoardFactory.fromEntity(it) }
            }

    override suspend fun insertKeepingState(board: Board) =
        (dao.get(board.id)
            ?.isFavorite ?: false)
            .let { isFavorite ->
                dao.insertBoard(
                    board.copy(isFavorite = isFavorite)
                        .let { BoardFactory.toEntity(it) }
                )
            }

    override suspend fun insertKeepingState(boards: List<Board>) =
        dao.getFavorites()
            .let { favorites ->
                dao.insertBoardList(
                    boards.map { board ->
                        board.copy(isFavorite = board.id in favorites)
                            .let { BoardFactory.toEntity(it) }
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
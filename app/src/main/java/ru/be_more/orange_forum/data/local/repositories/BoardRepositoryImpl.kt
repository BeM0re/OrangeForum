package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.extentions.disposables
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.domain.model.Board

class BoardRepositoryImpl (
    private val dao: DvachDao
) : DbContract.BoardRepository{

    override fun getBoards(): Single<List<Board>> =
        dao.getBoards()
            .map { boards ->
                boards.map { board ->
                    toModelBoard(board, listOf()) }
                }


    override fun getBoard(boardId: String, boardName: String): Single<Board> =
        dao.getBoard(boardId)
            .zipWith(dao.getThreads(boardId),
                BiFunction<List<StoredBoard>, List<StoredThread>, Board> { probablyBoard, threads ->
                    return@BiFunction if (probablyBoard.isNotEmpty())
                        toModelBoard(probablyBoard[0], toModelThreads(threads))
                    else
                        Board(boardName, boardId, toModelThreads(threads), false)
            })

/*    override fun getBoard(boardId: String): Single<List<Board>> =
        dao.getBoard(boardId)
            .zipWith(dao.getThreads(boardId),
                BiFunction<List<StoredBoard>, List<StoredThread>, List<Board>> { probablyBoard, threads ->
                    return@BiFunction if (probablyBoard.isNotEmpty())
                        listOf(toModelBoard(probablyBoard[0], toModelThreads(threads)))
                    else
                        listOf()
                })*/


    override fun getBoardCount(boardId: String): Single<Int> =
        dao.getBoardCount(boardId)

    override fun insertBoard(boardId: String, boardName: String, isFavorite: Boolean) =
        dao.insertBoard(StoredBoard(boardId, "", boardName, isFavorite))

    override fun markBoardFavorite(boardId: String, boardName: String) =
        dao.markBoardFavorite(boardId)

    override fun unmarkBoardFavorite(boardId: String) =
        dao.unmarkBoardFavorite(boardId)
}
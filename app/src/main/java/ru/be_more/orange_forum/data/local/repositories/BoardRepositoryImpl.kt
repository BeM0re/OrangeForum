package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.LocalContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.extentions.disposables
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.model.Board
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val dao: DvachDao
) : LocalContract.BoardRepository{

    override fun getBoard(boardId: String): Single<Board> =
        dao.getBoard(boardId)
            .zipWith(dao.getThreads(boardId),
                BiFunction<List<StoredBoard>, List<StoredThread>, Board> { probablyBoard, threads ->
                    return@BiFunction if (probablyBoard.isNotEmpty())
                        toModelBoard(probablyBoard[0], toModelThreads(threads))
                    else
                        Board("", boardId, toModelThreads(threads), false)
            })
            .processSingle()

    override fun markBoardFavorite(boardId: String, boardName: String): Single<Int> =
        dao.getBoardCount(boardId)
            .doOnSuccess { boardCount ->
                if (boardCount == 0)
                    dao.insertBoard(StoredBoard(boardId, "", boardName, true))
                else
                    dao.markBoardFavorite(boardId)
            }
            .processSingle()

    override fun unmarkBoardFavorite(boardId: String): Single<Unit> =
        Single.fromCallable { dao.unmarkBoardFavorite(boardId) }
            .processSingle()

    override fun release() {
        disposables.forEach{ it.dispose() }
        disposables.clear()
    }
}
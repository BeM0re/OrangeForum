package ru.be_more.orange_forum.data.local.repositories

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.data.local.LocalContract
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelBoard
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.model.Board
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val boardDao: BoardDao
) : LocalContract.BoardRepository{
    override fun getBoard(boardId: String): Observable<Board> =
        boardDao.getBoard(boardId)
            .observeOn(Schedulers.io())
            .zipWith(boardDao.getThreadsOnBoard(boardId), BiFunction { probablyBoard, threads ->
                return@BiFunction if (probablyBoard.isNotEmpty())
                    toModelBoard(probablyBoard[0], toModelThreads(threads))
                else
                    Board("", boardId, toModelThreads(threads), false)
            })

    override fun markBoardFavorite(boardId: String, boardName: String): Disposable =
        boardDao.getBoardCount(boardId)
            .observeOn(Schedulers.io())
            .subscribe (
                {
                    boardCount ->
                    if (boardCount == 0)
                        boardDao.insertBoard(StoredBoard(boardId, "", boardName, true))
                    else
                        boardDao.markBoardFavorite(boardId)
                },
                {
                    Log.e("M_DvachDbRepository","on favoriting a board error = $it")
                })

    override fun unmarkBoardFavorite(boardId: String): Disposable =
        Observable.fromCallable { boardDao.unmarkBoardFavorite(boardId) }
            .observeOn(Schedulers.io())
            .subscribe()
}
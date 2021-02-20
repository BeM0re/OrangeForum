package ru.be_more.orange_forum.domain.interactors

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.extentions.processCompletable
import ru.be_more.orange_forum.extentions.processSingle
import java.util.*

class BoardInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val dbBoardRepository: DbContract.BoardRepository,
    private val dbThreadRepository: DbContract.ThreadRepository
): InteractorContract.BoardInteractor {

    override fun getBoard(boardId: String, boardName: String): Single<Board> =
        Single.zip(
            dbBoardRepository.getBoard(boardId, boardName)
                .doOnError { Log.e("M_BoardInteractorImpl","get local board error = $it") },
            apiRepository.getDvachThreads(boardId)
                .doOnError { Log.e("M_BoardInteractorImpl","get web board error = $it") },
            BiFunction <Board, List<BoardThread>, Board> { localBoard, webThreads ->
                val resultThreads = LinkedList(webThreads)

                localBoard.threads.forEach { localThread ->
                    webThreads
                        .indexOfFirst { it.num == localThread.num }
                        .also{ webIndex ->
                            when (webIndex) {
                                -1 -> //удаляем инфу об утонувших несохраненных тредах
                                    dbThreadRepository.removeAllMarks(boardId, localThread.num)
                                else -> {
                                    val thread = resultThreads[webIndex]
                                    resultThreads.removeAt(webIndex)
                                    resultThreads.add(webIndex, thread.copy(
                                        isFavorite = localThread.isFavorite,
                                        isHidden = localThread.isHidden,
                                        isDownloaded = localThread.isDownloaded,
                                        isQueued = localThread.isQueued
                                    ))
                                }
                            }
                    }


                }
                return@BiFunction localBoard.copy(threads = resultThreads)
            }
        )
            .processSingle()

    override fun markBoardFavorite(boardId: String, boardName: String): Completable =
        dbBoardRepository.getBoardCount(boardId)
            .flatMapCompletable {boardCount ->
                if (boardCount == 0)
                    dbBoardRepository.insertBoard(boardId, boardName, true)
                else
                    dbBoardRepository.markBoardFavorite(boardId, boardName)
                Completable.complete()
            }
            .processCompletable()

    override fun unmarkBoardFavorite(boardId: String): Completable =
        Completable.fromCallable {
            dbBoardRepository.unmarkBoardFavorite(boardId)
        }
            .processCompletable()
}

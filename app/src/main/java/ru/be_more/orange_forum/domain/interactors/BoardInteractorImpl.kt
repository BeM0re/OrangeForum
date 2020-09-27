package ru.be_more.orange_forum.domain.interactors

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.extentions.disposables
import ru.be_more.orange_forum.extentions.processCompletable
import ru.be_more.orange_forum.extentions.processSingle

class BoardInteractorImpl(
    private val apiRepository: RemoteContract.BoardRepository,
    private val dbBoardRepository: DbContract.BoardRepository,
    private val dbThreadRepository: DbContract.ThreadRepository
): InteractorContract.BoardInteractor, BaseInteractorImpl() {

    override fun getBoard(boardId: String, boardName: String): Single<Board> =
        Single.zip(
            dbBoardRepository.getBoard(boardId, boardName),
            apiRepository.getDvachThreads(boardId),
            BiFunction <Board, List<BoardThread>, Board> { localBoard, webThreads ->

                localBoard.threads.forEach { localThread ->
                    val webIndex = webThreads.indexOfFirst { it.num == localThread.num }

                    if (webIndex == -1){ //удаляем инфу об утонувших несохраненных тредах
                        if (!localThread.isDownloaded)
                            dbThreadRepository.deleteThread(boardId, localThread.num)
                    }
                    else{ //TODO параметр webThreads иммутабелен, переделать
                        webThreads[webIndex].isFavorite = localThread.isFavorite
                        webThreads[webIndex].isHidden = localThread.isHidden
                        webThreads[webIndex].isDownloaded = localThread.isDownloaded
                    }
                }
                return@BiFunction localBoard.copy(threads = webThreads)
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


    override fun release() {
        Log.d("M_BaseInteractorImpl","release")
        disposables.forEach { it.dispose() }
        disposables.clear()
    }

}

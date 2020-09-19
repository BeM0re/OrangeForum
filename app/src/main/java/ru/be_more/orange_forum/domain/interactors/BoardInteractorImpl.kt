package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.extentions.processSingle

class BoardInteractorImpl(
    private val apiRepository: RemoteContract.BoardRepository,
    private val dbBoardRepository: DbContract.BoardRepository,
    private val dbThreadRepository: DbContract.ThreadRepository
): InteractorContract.BoardInteractor, BaseInteractorImpl() {

    override fun getBoard(boardId: String): Single<Board> =
        Single.zip(
            dbBoardRepository.getBoard(boardId),
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


    override fun markBoardFavorite(boardId: String, boardName: String): Single<Int> =
        dbBoardRepository.markBoardFavorite(boardId, boardName)
            .processSingle()

    override fun unmarkBoardFavorite(boardId: String): Single<Unit> =
        dbBoardRepository.unmarkBoardFavorite(boardId)
            .processSingle()

}

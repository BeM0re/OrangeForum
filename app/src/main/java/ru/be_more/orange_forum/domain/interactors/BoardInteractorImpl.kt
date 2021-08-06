package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board

class BoardInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val dbBoardRepository: DbContract.BoardRepository
): InteractorContract.BoardInteractor {

    override fun getBoard(boardId: String, boardName: String): Single<Board> =
        Single.zip(
            dbBoardRepository.getBoard(boardId)
                .switchIfEmpty(Single.just(Board(id = boardId, name = boardName))),
            apiRepository.getDvachThreads(boardId),
            { localBoard, webThreads ->
                localBoard.copy( threads =
                    webThreads
                        .map { webThread ->
                            webThread to localBoard.threads.firstOrNull{ webThread.num == it.num }
                        }
                        .map { (web, local) ->
                            web.copy(
                                isDownloaded = local?.isDownloaded ?: false,
                                isFavorite = local?.isFavorite ?: false,
                                isHidden = local?.isHidden ?: false,
                                isQueued = local?.isQueued ?: false
                            )
                        }
                )
            }
        )

    override fun markBoardFavorite(boardId: String, boardName: String): Completable =
        dbBoardRepository.getBoardCount(boardId)
            .flatMapCompletable {boardCount ->
                if (boardCount == 0)
                    dbBoardRepository.insertBoard(boardId, boardName, true)
                else
                    dbBoardRepository.markBoardFavorite(boardId, boardName)
                Completable.complete()
            }

    override fun unmarkBoardFavorite(boardId: String): Completable =
        Completable.fromCallable {
            dbBoardRepository.unmarkBoardFavorite(boardId)
        }
}

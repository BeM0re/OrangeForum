package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Observable
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.model.Board

class QueueInteractorImpl(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val apiRepository: RemoteContract.ApiRepository,
): InteractorContract.QueueInteractor{

    override fun observe(): Observable<List<Board>> =
        deleteDrawnThreads()
            .andThen(
                Observable.combineLatest(
                    boardRepository.observeList(),
                    threadRepository.observeQueued()
                ) { boards, threads ->
                    boards
                        .map { board ->
                            board to threads.filter { it.boardId == board.id }
                        }
                        .map { (board, threads) ->
                            board.copy(threads = threads)
                        }
                        .filter { it.threads.isNotEmpty() }
                }
            )

    override fun clear(): Completable =
        threadRepository.markQueuedAll(isQueued = false)

    private fun deleteDrawnThreads() =
        threadRepository.getQueued()
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapCompletable { thread ->
                apiRepository.getThreadInfo(thread.boardId, thread.num)
                    .map { !it.isAlive }
                    .flatMapCompletable {
                        threadRepository.delete(thread.boardId, thread.num)
                    }
            }
}
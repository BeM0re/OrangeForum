package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Observable
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board

class QueueInteractorImpl(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
): InteractorContract.QueueInteractor{

    override fun observe(): Observable<List<Board>> =
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
}
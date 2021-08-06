package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board

class QueueInteractorImpl(
    private val boardRepository: DbContract.BoardRepository
): InteractorContract.QueueInteractor{

    override fun getQueue(): Single<List<Board>> =
        boardRepository.getBoards()
            .map { boardList ->
                boardList
                    .map { board ->
                        board.copy(threads = board.threads.filter { it.isQueued })
                    }
                    .filter { it.threads.isNotEmpty() }
            }

}
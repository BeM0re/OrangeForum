package ru.be_more.orange_forum.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.model.Board

class QueueInteractorImpl(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val apiRepository: RemoteContract.ApiRepository,
): InteractorContract.QueueInteractor{

    override fun getFlow(): Flow<List<Board>> =
        run {
            combine(
                boardRepository.getListFlow(),
                threadRepository.getQueuedFlow()
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

    override suspend fun clear() =
        threadRepository.markQueuedAll(isQueued = false)

    private suspend fun deleteDrownThreads() =
        threadRepository.getQueued()
            .forEach { thread ->
                apiRepository.getThreadInfo(thread.boardId, thread.num)
                    .also {
                        if (!it.isAlive)
                            threadRepository.delete(it.boardId, it.threadNum)
                    }
            }
}
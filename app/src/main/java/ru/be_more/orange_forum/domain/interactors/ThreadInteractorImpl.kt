package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread

class ThreadInteractorImpl (
    private val apiRepository: RemoteContract.ApiRepository,
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository
): InteractorContract.ThreadInteractor {

    override fun getThread(
        boardId: String,
        threadNum: Int,
        forceUpdate: Boolean
    ): Single<BoardThread> =
        Single.zip(
            threadRepository.getThread(boardId, threadNum)
                .switchIfEmpty(Single.just(BoardThread.empty())),
            apiRepository.getThread(boardId, threadNum, forceUpdate),
            { localThread, webThread ->
                if (localThread.isEmpty())
                    webThread
                else {
                    threadRepository.updateLastPostNum(boardId, threadNum, webThread.lastPostNumber)
                    webThread.copy(
                        isHidden = localThread.isHidden,
                        isFavorite = localThread.isFavorite,
                        isDownloaded = localThread.isDownloaded,
                        isQueued = localThread.isQueued
                    )
                }
            }
        )

    override fun markThreadFavorite(
        boardId: String,
        boardName: String,
        threadNum: Int,
        isFavorite: Boolean
    ): Completable {
        return threadRepository.getThread(boardId, threadNum)
            .switchIfEmpty(apiRepository.getThreadShort(boardId, threadNum))
            .doOnSuccess { threadRepository.insertThread(it.copy(isFavorite = isFavorite), boardId) }
            .flatMap { thread ->
                boardRepository.getBoard(boardId)
                    .switchIfEmpty(Single.just(Board(id = boardId, name = boardName)))
                    .map { board ->
                        board.threads.firstOrNull{ it.num == threadNum }?.copy(isFavorite = isFavorite)
                            ?: thread.copy(isFavorite = isFavorite, posts = listOf(thread.posts.first()))
                    }
            }
            .flatMapCompletable {
                boardRepository.insertThreadIntoBoard(boardId, boardName, it)
            }
    }

    //todo make worker for this
    override fun downloadThread(
        boardId: String,
        boardName: String,
        threadNum: Int
    ): Completable {
        return apiRepository.getThread(boardId, threadNum)
            .doOnSuccess { threadRepository.saveThread(it.copy(isDownloaded = true), boardId) }
            .flatMap { thread ->
                boardRepository.getBoard(boardId)
                    .switchIfEmpty(Single.just(Board(id = boardId, name = boardName)))
                    .map { board ->
                        board.threads.firstOrNull{ it.num == threadNum }?.copy(isDownloaded = true)
                            ?: thread.copy(isDownloaded = true, posts = listOf(thread.posts.first()))
                    }
            }
            .flatMapCompletable {
                boardRepository.insertThreadIntoBoard(boardId, boardName, it)
            }
    }

    //todo make worker for this
    override fun deleteThread(boardId: String, threadNum: Int) =
        threadRepository.deleteThread(boardId, threadNum)

    override fun markThreadQueued(
        boardId: String,
        boardName: String,
        threadNum: Int,
        isQueued: Boolean
    ): Completable {
        return threadRepository.getThread(boardId, threadNum)
            .switchIfEmpty(apiRepository.getThreadShort(boardId, threadNum))
            .doOnSuccess { threadRepository.insertThread(it.copy(isQueued = isQueued), boardId) }
            .flatMap { thread ->
                boardRepository.getBoard(boardId)
                    .switchIfEmpty(Single.just(Board(id = boardId, name = boardName)))
                    .map { board ->
                        board.threads.firstOrNull{ it.num == threadNum }?.copy(isQueued = isQueued)
                            ?: thread.copy(isQueued = isQueued)
                    }
            }
            .flatMapCompletable {
                boardRepository.insertThreadIntoBoard(boardId, boardName, it)
            }
    }

    override fun markThreadHidden(
        boardId: String,
        boardName: String,
        threadNum: Int,
        isHidden: Boolean
    ): Completable {
        return threadRepository.getThread(boardId, threadNum)
            .switchIfEmpty(apiRepository.getThreadShort(boardId, threadNum))
            .doOnSuccess { threadRepository.insertThread(it.copy(isHidden = isHidden), boardId) }
            .flatMap { thread ->
                boardRepository.getBoard(boardId)
                    .switchIfEmpty(Single.just(Board(id = boardId, name = boardName)))
                    .map { board ->
                        board.threads.firstOrNull{ it.num == threadNum }?.copy(isHidden = isHidden)
                            ?: thread.copy(isHidden = isHidden, posts = listOf(thread.posts.first()))
                    }
            }
            .flatMapCompletable {
                boardRepository.insertThreadIntoBoard(boardId, boardName, it)
            }
    }
}
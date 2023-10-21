package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Observable
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.model.Board
import java.util.concurrent.TimeUnit

class FavoriteInteractorImpl(
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val apiRepository: RemoteContract.ApiRepository,
): InteractorContract.FavoriteInteractor {

    override fun observe(): Observable<List<Board>> =
        updateThreadInfo()
            .andThen(
                Observable.combineLatest(
                    boardRepository.observeList(),
                    threadRepository.observeFavorite()
                ) { boards, threads ->
                    boards
                        .map { board ->
                            board to threads.filter { it.boardId == board.id }
                        }
                        .map { (board, threads) ->
                            board.copy(threads = threads)
                        }
                        .filter { it.threads.isNotEmpty() || it.isFavorite }
                }
            )

    override fun observeNewMessage(): Completable =
        Observable.interval(1, 1, TimeUnit.MINUTES)
            .flatMapCompletable { updateThreadInfo() }

    private fun updateThreadInfo() =
        threadRepository.getFavorites()
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapCompletable { thread ->
                apiRepository.getThreadInfo(thread.boardId, thread.num)
                    .flatMapCompletable {info ->
                        when {
                            !info.isAlive ->
                                threadRepository.setIsDrown(info.boardId, info.threadNum, isDrown = true)
                            info.postCount > thread.postCount ->
                                threadRepository.setPostCount(info.boardId, info.threadNum, info.postCount)
                                    .andThen(
                                        threadRepository.setHasNewPost(info.boardId, info.threadNum, hasNewPost = true)
                                    )
                            else ->
                                Completable.complete()
                        }

                    }
            }
}
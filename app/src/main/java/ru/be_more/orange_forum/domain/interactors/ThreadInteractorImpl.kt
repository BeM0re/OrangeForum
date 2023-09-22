package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.consts.ThreadUpdateInterval
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.BoardThread
import java.util.concurrent.TimeUnit

class ThreadInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val boardRepository: DbContract.BoardRepository,
    private val threadRepository: DbContract.ThreadRepository,
    private val postRepository: DbContract.PostRepository
): InteractorContract.ThreadInteractor {

    override fun observe(
        boardId: String,
        threadNum: Int,
    ): Observable<BoardThread> =
        downloadThread(boardId, threadNum)
            .andThen(
                Observable.combineLatest(
                    threadRepository.observe(boardId, threadNum),
                    postRepository.observe(boardId, threadNum)
                ) { thread, posts ->
                    thread.copy(posts = posts)
                }
            )

    override fun markFavorite(
        boardId: String,
        boardName: String,
        threadNum: Int,
    ): Completable =
        threadRepository
            .get(boardId, threadNum)
            .flatMapCompletable {
                threadRepository.markFavorite(boardId, threadNum, !it.isFavorite)
            }

    override fun markQueued(boardId: String, boardName: String, threadNum: Int): Completable =
        threadRepository
            .get(boardId, threadNum)
            .flatMapCompletable {
                threadRepository.markQueued(boardId, threadNum, !it.isQueued)
            }

    override fun markHidden(boardId: String, boardName: String, threadNum: Int): Completable =
        threadRepository
            .get(boardId, threadNum)
            .flatMapCompletable {
                threadRepository.markHidden(boardId, threadNum, !it.isHidden)
            }

    override fun subToUpdate(boardId: String, threadNum: Int): Completable =
        Observable
            .interval(ThreadUpdateInterval, TimeUnit.SECONDS)
            .flatMapCompletable { downloadThread(boardId, threadNum) }

    @Deprecated("Maybe delete")
    override fun updateNewMessages(boardId: String, threadNum: Int): Completable =
        Single
            .zip(
                threadRepository.get(boardId, threadNum)
                    .switchIfEmpty(
                        Single.error(Throwable("ThreadInteractorImpl: trying to update null thread"))
                    ),
                apiRepository.getThread(boardId, threadNum, true)
            ) { local, web ->
                web.posts.filter { it.id > local.lastPostNumber }.size
            }
            .flatMapCompletable {
                boardRepository.updateThreadNewMessageCounter(boardId, threadNum, it)
            }

    @Deprecated("Maybe delete")
    override fun updateNewMessages(
        boardId: String,
        threadNum: Int,
        newMessageCount: Int
    ): Completable =
        boardRepository.updateThreadNewMessageCounter(boardId, threadNum, newMessageCount)

    @Deprecated("Maybe delete")
    override fun updateLastPostNum(
        boardId: String,
        threadNum: Int,
        lastPostNum: Int
    ): Completable {
        return Completable.fromCallable {
            threadRepository.updateLastPostNum(boardId, threadNum, lastPostNum)
        }
    }

    override fun delete(boardId: String, threadNum: Int) =
        threadRepository.delete(boardId, threadNum)
            .andThen(postRepository.delete(boardId, threadNum))

    private fun downloadThread(
        boardId: String,
        threadNum: Int,
    ): Completable =
        apiRepository.getThread(boardId, threadNum)
            .flatMapCompletable {
                //todo save states
                threadRepository.save(it.copy(isDownloaded = true), boardId)
                    .andThen(postRepository.insert(it.posts))
            }
}
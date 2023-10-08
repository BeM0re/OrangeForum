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
        getThread(boardId, threadNum, savePics = false)
            .andThen(
                Observable.combineLatest(
                    threadRepository.observe(boardId, threadNum),
                    postRepository.observe(boardId, threadNum)
                ) { thread, posts ->
                    thread.copy(posts = posts)
                }
            )

    override fun save(boardId: String, threadNum: Int): Completable =
        getThread(boardId,threadNum, savePics = true)

    override fun markFavorite(boardId: String, threadNum: Int): Completable =
        threadRepository
            .get(boardId, threadNum)
            .flatMapCompletable {
                threadRepository.markFavorite(boardId, threadNum, !it.isFavorite)
            }

    override fun markQueued(boardId: String, threadNum: Int): Completable =
        threadRepository
            .get(boardId, threadNum)
            .flatMapCompletable {
                threadRepository.markQueued(boardId, threadNum, !it.isQueued)
            }

    override fun markHidden(boardId: String, threadNum: Int): Completable =
        threadRepository
            .get(boardId, threadNum)
            .flatMapCompletable {
                threadRepository.markHidden(boardId, threadNum, !it.isHidden)
            }

    override fun subToUpdate(boardId: String, threadNum: Int): Completable =
        Observable
            .interval(ThreadUpdateInterval, TimeUnit.SECONDS)
            .flatMapCompletable { getThread(boardId, threadNum, savePics = false) }

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

    override fun delete(boardId: String, threadNum: Int): Completable =
        threadRepository.delete(boardId, threadNum)
            .andThen(postRepository.delete(boardId, threadNum))

    private fun getThread(
        boardId: String,
        threadNum: Int,
        savePics: Boolean,
    ): Completable =
        apiRepository.getThread(boardId, threadNum)
            .flatMapCompletable { thread ->
                //todo save states
                if (savePics)
                    threadRepository
                        .insert(thread.copy(isDownloaded = true))
                        .andThen(postRepository.save(thread.posts))
                        .flatMapCompletable { postRepository.insert(it) }
                else
                    threadRepository
                        .insert(thread)
                        .andThen(postRepository.insert(thread.posts))
            }

}
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
        Observable.combineLatest(
            threadRepository.observe(boardId, threadNum),
            postRepository.observe(boardId, threadNum)
        ) { thread, posts ->
            thread.copy(posts = posts)
        }

    override fun save(boardId: String, threadNum: Int): Completable =
        apiRepository.getThread(boardId, threadNum)
            .flatMapCompletable { thread ->
                threadRepository
                    .insertKeepingState(
                        listOf(thread.copy(isDownloaded = true))
                    )
                    .andThen(postRepository.save(thread.posts))
            }

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

    override fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int): Completable =
        threadRepository.updateLastPostViewed(boardId, threadNum, postNum)

    //todo для докачивания постов есть отдельный метод апи
    override fun subToUpdate(boardId: String, threadNum: Int): Completable =
        Observable
            .interval(ThreadUpdateInterval, TimeUnit.SECONDS)
            .flatMapCompletable { getThread(boardId, threadNum, savePics = false) }

    override fun delete(boardId: String, threadNum: Int): Completable =
        threadRepository.delete(boardId, threadNum)
            .andThen(postRepository.delete(boardId, threadNum))

    override fun refresh(boardId: String, threadNum: Int): Completable =
        apiRepository.getThread(boardId, threadNum)
            .flatMapCompletable { thread ->
                threadRepository.insertKeepingState(listOf(thread))
                    .andThen(
                        postRepository.insertMissing(thread)
                    )
            }

    @Deprecated("")
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

                else
                    threadRepository
                        .insert(thread)
                        .andThen(postRepository.insert(thread.posts))
            }

}
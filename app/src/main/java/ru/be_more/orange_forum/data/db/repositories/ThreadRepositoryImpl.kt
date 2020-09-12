package ru.be_more.orange_forum.data.db.repositories

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.be_more.orange_forum.data.db.DbContract
import ru.be_more.orange_forum.data.db.db.dao.DvachDao
import ru.be_more.orange_forum.data.db.db.entities.StoredBoard
import ru.be_more.orange_forum.data.db.db.entities.StoredThread
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.downloadedToStoredThread
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.favoriteToStoredThread
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelThread
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.extentions.processSingle
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.extentions.processCompletable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadRepositoryImpl @Inject constructor(
    private val dao: DvachDao
) : DbContract.ThreadRepository {

    @SuppressLint("CheckResult")
    override fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>> =
        dao.getThreadOrEmpty(boardId, threadNum)
            .map { toModelThreads(it) }
            .processSingle()

    override fun getDownloadedThreads(): Single<List<Pair<BoardThread, String>>> =
        dao.getDownloadedThreads()
            .map { threads ->
                threads.map {
                    Pair(toModelThread(it, listOf()), it.boardId)
                }
            }
            .processSingle()



    override fun insertThread(thread: BoardThread, boardId: String) {
        dao.insertThread(downloadedToStoredThread(thread, boardId))
    }


    @SuppressLint("CheckResult")
    //TODO вынести обращение в интерактор
    override fun downloadThread(thread: BoardThread, boardId: String) =
        Completable.create { emitter ->
            Single.zip(dao.getBoardCount(boardId),
                dao.getThreadCount(boardId, thread.num),
                BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                    if (boardCount == 0){
                        dao.insertBoard(StoredBoard(boardId, "", boardId, false))
                    }
                    if (threadCount == 0) {
                        dao.insertThread(downloadedToStoredThread(thread, boardId))
                    }
                    else{
                        dao.markThreadDownload(boardId, thread.num)
                    }
                }
            ).processSingle()
            .subscribe({emitter.onComplete()}, emitter::onError)
        }


    override fun deleteThread(boardId: String, threadNum: Int) =
        Completable.fromCallable { dao.deleteThread(boardId, threadNum) }
            .processCompletable()


    //TODO добавить сохранение 1 поста
    @SuppressLint("CheckResult")
    override fun markThreadFavorite(
        thread: BoardThread,
        boardId: String,
        boardName: String ) =
        Completable.create { emitter ->
            Single.zip(dao.getBoardCount(boardId),
                dao.getThreadCount(boardId, thread.num),
                BiFunction <Int, Int, Unit> { boardCount, threadCount ->
                    if (boardCount == 0){
                        dao.insertBoard(StoredBoard(boardId, "", boardName, false))
                    }
                    if (threadCount == 0) {
                        dao.insertThread(favoriteToStoredThread(thread, boardId))
                    }
                    else
                        dao.markThreadFavorite(boardId, thread.num)
                }
            ).processSingle()
            .subscribe({emitter.onComplete()}, emitter::onError)
        }


    override fun unmarkThreadFavorite(boardId: String, threadNum: Int) =
        Completable.fromCallable { dao.unmarkThreadFavorite(boardId, threadNum) }
            .processCompletable()

    override fun markThreadHidden(boardId: String, threadNum: Int) =
        Completable.create {emitter ->
            dao.getThreadOrEmpty(boardId, threadNum)
                .doOnSuccess { thread ->
                    if (thread.isNotEmpty())
                        dao.markThreadHidden(boardId, threadNum)
                    else
                        dao.insertThread(
                            StoredThread(
                                threadNum,
                                "",
                                boardId,
                                isHidden = true)
                        )
                }
                .processSingle()
                .subscribe({emitter.onComplete()}, emitter::onError)
        }

    override fun unmarkThreadHidden(boardId: String, threadNum: Int) =
        Completable.create { emitter ->
            Single.fromCallable { dao.unmarkThreadHidden(boardId, threadNum) }
                .processSingle()
                .subscribe({emitter.onComplete()}, emitter::onError)
        }
}
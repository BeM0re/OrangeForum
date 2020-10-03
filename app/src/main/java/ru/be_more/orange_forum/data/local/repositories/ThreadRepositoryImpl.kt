package ru.be_more.orange_forum.data.local.repositories

import android.annotation.SuppressLint
import io.reactivex.Single
import ru.be_more.orange_forum.domain.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredThread
import ru.be_more.orange_forum.domain.model.BoardThread

class ThreadRepositoryImpl(
    private val dao: DvachDao
) : DbContract.ThreadRepository {

    @SuppressLint("CheckResult")
    override fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>> =
        dao.getThreadOrEmpty(boardId, threadNum)
            .map { toModelThreads(it) }

    override fun getDownloadedThreads(): Single<List<Pair<BoardThread, String>>> =
        dao.getDownloadedThreads()
            .map { threads ->
                threads.map {
                    Pair(toModelThread(it, listOf()), it.boardId)
                }
            }

    override fun insertThread(thread: BoardThread, boardId: String) =
        dao.insertThread(toStoredThread(thread, boardId))

    override fun insertThreadSafety(thread: BoardThread, boardId: String): Single<Boolean> =
        dao.getThreadOrEmpty(boardId, thread.num)
            .map { probablyThread ->
                if (probablyThread.isEmpty()) {
                    dao.insertThread(toStoredThread(thread, boardId))
                    return@map true
                }
                else
                    return@map false
            }

    override fun deleteThread(boardId: String, threadNum: Int) {
        dao.deleteThread(boardId, threadNum)
        dao.deletePosts(boardId, threadNum)
        dao.deleteThreadFiles(boardId, threadNum)
    }

    override fun markThreadFavorite(boardId: String, threadNum: Int) =
        dao.markThreadFavorite(boardId, threadNum)

    override fun unmarkThreadFavorite(boardId: String, threadNum: Int) =
        dao.unmarkThreadFavorite(boardId, threadNum)


    override fun markThreadHidden(boardId: String, threadNum: Int) =
         dao.markThreadHidden(boardId, threadNum)


    override fun unmarkThreadHidden(boardId: String, threadNum: Int) =
        dao.unmarkThreadHidden(boardId, threadNum)
}
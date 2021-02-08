package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThreads
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredThread
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.BoardThread

class ThreadRepositoryImpl(
    private val dao: DvachDao,
    private val storage: StorageContract.LocalStorage
) : DbContract.ThreadRepository {

    override fun getThreadOrEmpty(boardId: String, threadNum: Int): Single<List<BoardThread>> =
        dao.getThreadOrEmpty(boardId, threadNum)
            .map { toModelThreads(it) }

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

    override fun deleteThread(boardId: String, threadNum: Int): Completable =
        Completable.fromSingle(
            dao.getNonOpFilesFromThread(threadNum, boardId)
                .doOnSuccess { files ->
                    files.forEach { file ->
                        storage.removeFile(file.localPath)
                        storage.removeFile(file.localThumbnail)
                    }
                    dao.deleteThreadFiles(boardId, threadNum)
                    dao.deletePosts(boardId, threadNum)
                    dao.deleteThread(boardId, threadNum)
                }
        )

    override fun markThreadDownloaded(boardId: String, threadNum: Int)=
        dao.markThreadDownloaded(boardId, threadNum)

    override fun addThreadToQueue(boardId: String, threadNum: Int) =
        dao.addThreadToQueue(boardId, threadNum)

    override fun addThreadToFavorite(boardId: String, threadNum: Int) =
        dao.addThreadToFavorite(boardId, threadNum)

    override fun removeThreadFromFavorite(boardId: String, threadNum: Int) =
        dao.removeThreadFromFavorite(boardId, threadNum)

    override fun removeThreadFromQueue(boardId: String, threadNum: Int) =
        dao.removeThreadFromQueue(boardId, threadNum)

    override fun hideThread(boardId: String, threadNum: Int) =
         dao.markThreadHidden(boardId, threadNum)

    override fun unhideThread(boardId: String, threadNum: Int) =
        dao.unmarkThreadHidden(boardId, threadNum)
}
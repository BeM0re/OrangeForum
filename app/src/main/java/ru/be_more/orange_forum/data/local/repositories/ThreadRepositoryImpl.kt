package ru.be_more.orange_forum.data.local.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.ThreadDao
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.BoardThread
import javax.inject.Inject

class ThreadRepositoryImpl @Inject constructor(
    private val dao: ThreadDao,
    private val storage: StorageContract.LocalStorage
) : DbContract.ThreadRepository {

    override suspend fun insert(thread: BoardThread) =
        dao.insert(StoredThread(thread))

    override suspend fun insertKeepingState(threads: List<BoardThread>) =
        run {
            val favoriteIds = dao.getFavoriteIdsSync()
            val queuedIds = dao.getQueuedIdsSync()
            val downloadedIds = dao.getDownloadIdsSync()
            val hiddenIds = dao.getHiddenIdsSync()
            val lastReadPostMap = dao.getLastReadPost()

            threads.map { thread ->
                thread.copy(
                    isFavorite = thread.num in favoriteIds,
                    isQueued = thread.num in queuedIds,
                    isDownloaded = thread.num in downloadedIds || thread.isDownloaded,
                    isHidden = thread.num in hiddenIds,
                    lastPostRead = lastReadPostMap.getOrDefault(thread.num, 0)
                )
            }
        }.let { editedThreads ->
            dao.insert(
                editedThreads.map { StoredThread(it) }
            )
        }

    override suspend fun save(thread: BoardThread, boardId: String) {
        return dao.insert(
            StoredThread(
                thread = thread.copy(
                    boardId = boardId,
                    posts = thread.posts.map { post ->
                        post.copy(
                            files = post.files.map { file ->
                                file.copy(
                                    localPath = storage.saveFile(file.path).toString(),
                                    localThumbnail = storage.saveFile(file.thumbnail).toString()
                                )
                            }
                        )
                    }
                ),
            )
        )
    }


    override suspend fun get(boardId: String, threadNum: Int): BoardThread? =
        dao.get(boardId, threadNum)
            ?.toModel()

    override suspend fun getFavorites(): List<BoardThread> =
        dao.getFavorites()
            .map { it.toModel() }

    override suspend fun getQueued(): List<BoardThread> =
        dao.getQueued()
            .map { it.toModel() }

    override fun getFlow(boardId: String, threadNum: Int): Flow<BoardThread> =
        dao.getFlow(boardId, threadNum)
            .map { it.toModel() }

    override  fun getListFlow(boardId: String): Flow<List<BoardThread>> =
        dao.getListFlow(boardId)
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun getFavoriteFlow(): Flow<List<BoardThread>> =
        dao.getFavoriteFlow()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun getQueuedFlow(): Flow<List<BoardThread>> =
        dao.getQueuedFlow()
            .map { threads ->
                threads.map { it.toModel() }
            }


    override suspend fun setPostCount(boardId: String, threadNum: Int, postNum: Int) =
        dao.setPostCount(boardId, threadNum, postNum)

    override suspend fun setLasthit(boardId: String, threadNum: Int, lasthit: Long) =
        dao.setLasthit(boardId, threadNum, lasthit)

    override suspend fun setHasNewPost(boardId: String, threadNum: Int, hasNewPost: Boolean) =
        dao.setHasNewPost(boardId, threadNum, hasNewPost)

    override suspend fun setIsDrown(boardId: String, threadNum: Int, isDrown: Boolean) =
        dao.setIsDrown(boardId, threadNum, isDrown)

    override suspend fun markFavorite(boardId: String, threadNum: Int, isFavorite: Boolean) =
        dao.setIsFavorite(boardId, threadNum, isFavorite)

    override suspend fun markHidden(boardId: String, threadNum: Int, isHidden: Boolean) =
        dao.setIsHidden(boardId, threadNum, isHidden)

    override suspend fun markQueued(boardId: String, threadNum: Int, isQueued: Boolean) =
        dao.setIsQueue(boardId, threadNum, isQueued)

    override suspend fun markQueuedAll(isQueued: Boolean) =
        dao.setIsQueueForAll(isQueued)

    override suspend fun updateLastPostViewed(boardId: String, threadNum: Int, postNum: Int) =
        dao.updateLastPostViewed(boardId, threadNum, postNum)


    override suspend fun delete(boardId: String, threadNum: Int) =
        dao.delete(boardId, threadNum)

    override suspend fun deleteKeepingState(boardId: String) =
        dao.deleteKeepingState(boardId)

    override suspend fun deleteExceptGiven(boardId: String, liveThreadNumList: List<Int>) =
        dao.deleteExceptGiven(boardId, liveThreadNumList)
}
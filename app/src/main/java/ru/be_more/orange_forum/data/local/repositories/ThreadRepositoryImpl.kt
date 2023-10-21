package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.ThreadDao
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.BoardThread

class ThreadRepositoryImpl(
    private val dao: ThreadDao,
    private val storage: StorageContract.LocalStorage
) : DbContract.ThreadRepository {

    override fun observe(boardId: String, threadNum: Int): Observable<BoardThread> =
        dao.observe(boardId, threadNum)
            .map { it.toModel() }

    override fun get(boardId: String, threadNum: Int): Maybe<BoardThread> =
        dao.get(boardId, threadNum)
            .map { it.toModel() }

    override fun getFavorites(): Single<List<BoardThread>> =
        dao.getFavorites()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun getQueued(): Single<List<BoardThread>> =
        dao.getQueued()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun observeList(boardId: String): Observable<List<BoardThread>> =
        dao.observeList(boardId)
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun observeFavorite(): Observable<List<BoardThread>> =
        dao.observeFavorites()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun observeQueued(): Observable<List<BoardThread>> =
        dao.observeQueue()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun insert(thread: BoardThread): Completable =
        dao.insert(StoredThread(thread))

    override fun insertKeepingState(threads: List<BoardThread>): Completable =
        Single
            .fromCallable {
                val favoriteIds = dao.getFavoriteIdsSync()
                val queuedIds = dao.getQueuedIdsSync()
                val downloadedIds = dao.getDownloadIdsSync()
                val hiddenIds = dao.getHiddenIdsSync()

                threads.map { thread ->
                    thread.copy(
                        isFavorite = thread.num in favoriteIds,
                        isQueued = thread.num in queuedIds,
                        isDownloaded = thread.num in downloadedIds || thread.isDownloaded,
                        isHidden = thread.num in hiddenIds,
                    )
                }
            }
            .flatMapCompletable { editedThreads ->
                dao.insert(
                    editedThreads.map { StoredThread(it) }
                )
            }

    override fun save(thread: BoardThread, boardId: String): Completable {
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

    override fun setPostCount(boardId: String, threadNum: Int, postNum: Int) =
        dao.setPostCount(boardId, threadNum, postNum)

    override fun setHasNewPost(boardId: String, threadNum: Int, hasNewPost: Boolean): Completable =
        dao.setHasNewPost(boardId, threadNum, hasNewPost)

    override fun setIsDrown(boardId: String, threadNum: Int, isDrown: Boolean): Completable =
        dao.setIsDrown(boardId, threadNum, isDrown)

    override fun markFavorite(boardId: String, threadNum: Int, isFavorite: Boolean): Completable =
        dao.setIsFavorite(boardId, threadNum, isFavorite)

    override fun markHidden(boardId: String, threadNum: Int, isHidden: Boolean): Completable =
        dao.setIsHidden(boardId, threadNum, isHidden)

    override fun markQueued(boardId: String, threadNum: Int, isQueued: Boolean): Completable =
        dao.setIsQueue(boardId, threadNum, isQueued)

    override fun markQueuedAll(isQueued: Boolean): Completable =
        dao.setIsQueueForAll(isQueued)

    override fun delete(boardId: String, threadNum: Int): Completable =
        dao.delete(boardId, threadNum)

    override fun deleteKeepingState(boardId: String): Completable =
        dao.deleteKeepingState(boardId)

    override fun deleteExceptGiven(boardId: String, liveThreadNumList: List<Int>): Completable =
        dao.deleteExceptGiven(boardId, liveThreadNumList)
}
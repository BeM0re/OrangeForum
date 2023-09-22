package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
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

    override fun observeList(boardId: String): Observable<List<BoardThread>> =
        dao.observeList(boardId)
            .map { threads ->
                threads.map { it.toModel() }
            }

    //todo obaservable or single?
    override fun observeFavorite(): Observable<List<BoardThread>> =
        dao.getFavorites()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun observeQueued(): Observable<List<BoardThread>> =
        dao.getQueue()
            .map { threads ->
                threads.map { it.toModel() }
            }

    override fun insert(thread: BoardThread): Completable =
        dao.insert(StoredThread(thread))

    override fun insert(threads: List<BoardThread>): Completable =
        dao.insert(
            threads.map { StoredThread(it) }
        )

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

    //todo keep hidden?
    override fun delete(boardId: String, threadNum: Int): Completable =
        dao.delete(boardId, threadNum)

    override fun delete(boardId: String): Completable=
        dao.delete(boardId)

    override fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int) =
        dao.updateLastPostNum(boardId, threadNum, postNum)

    override fun markFavorite(boardId: String, threadNum: Int, isFavorite: Boolean): Completable =
        dao.setIsFavorite(boardId, threadNum, isFavorite)

    override fun markHidden(boardId: String, threadNum: Int, isHidden: Boolean): Completable =
        dao.setIsHidden(boardId, threadNum, isHidden)

    override fun markQueued(boardId: String, threadNum: Int, isQueued: Boolean): Completable =
        dao.setIsQueue(boardId, threadNum, isQueued)
}
package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelThread
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredThread
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.BoardThread

class ThreadRepositoryImpl(
    private val dao: DvachDao,
    private val storage: StorageContract.LocalStorage
) : DbContract.ThreadRepository {

    override fun getThread(boardId: String, threadNum: Int): Maybe<BoardThread> =
        dao.getThread(boardId, threadNum)
            .map { toModelThread(it) }

    override fun insertThread(thread: BoardThread, boardId: String) =
        dao.insertThread(toStoredThread(thread, boardId))

    override fun saveThread(thread: BoardThread, boardId: String) {
        return dao.insertThread(
            toStoredThread(
                thread = thread.copy(
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
                boardId = boardId
            )
        )
    }

    override fun deleteThread(boardId: String, threadNum: Int): Completable {
        return dao.getThread(boardId, threadNum)
            .doOnSuccess { thread ->
                thread.posts.forEach { post ->
                    post.files.forEach { file ->
                        storage.removeFile(file.localPath)
                        storage.removeFile(file.localThumbnail)
                    }
                }
            }
            .map { it.copy(posts = it.posts.subList(0, 1), isDownloaded = false) }
            .doOnSuccess { dao.insertThread(it) }
            .ignoreElement()
    }

    override fun updateLastPostNum(boardId: String, threadNum: Int, postNum: Int) =
        dao.updateLastPostNum(boardId, threadNum, postNum)

}
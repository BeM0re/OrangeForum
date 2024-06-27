package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredThread
import ru.be_more.model.model.BoardThread

object ThreadFactory {

    fun fromEntity(storedBoard: StoredThread): BoardThread =
        BoardThread(
            num = storedBoard.num,
            boardId = storedBoard.boardId,
            title = storedBoard.title,
            lastPostNumber = storedBoard.lastPostNumber,
            newMessageAmount = storedBoard.newMessageAmount,
            postCount = storedBoard.postCount,
            fileCount = storedBoard.fileCount,
            lasthit = storedBoard.lasthit,
            lastPostRead = storedBoard.lastPostRead,
            isPinned = storedBoard.isPinned,
            isHidden = storedBoard.isHidden,
            isDownloaded = storedBoard.isDownloaded,
            isFavorite = storedBoard.isFavorite,
            isQueued = storedBoard.isQueued,
            isDrown = storedBoard.isDrown,
            hasNewMessages = storedBoard.hasNewMessages,
        )

    fun toEntity(thread: BoardThread) =
        StoredThread(
            num = thread.num,
            boardId = thread.boardId,
            title = thread.title,
            lastPostNumber = thread.lastPostNumber,
            newMessageAmount = thread.newMessageAmount,
            postCount = thread.postCount,
            fileCount = thread.fileCount,
            lasthit = thread.lasthit,
            lastPostRead = thread.lastPostRead,
            isPinned = thread.isPinned,
            isHidden = thread.isHidden,
            isDownloaded = thread.isDownloaded,
            isFavorite = thread.isFavorite,
            isQueued = thread.isQueued,
            isDrown = thread.isDrown,
            hasNewMessages = thread.hasNewMessages,
        )
}
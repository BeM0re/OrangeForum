package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredThread
import ru.be_more.model.model.BoardThread

fun StoredThread.toModel(): BoardThread =
    BoardThread(
        num = num,
        boardId = boardId,
        title = title,
        lastPostNumber = lastPostNumber,
        newMessageAmount = newMessageAmount,
        postCount = postCount,
        fileCount = fileCount,
        lasthit = lasthit,
        lastPostRead = lastPostRead,
        isPinned = isPinned,
        isHidden = isHidden,
        isDownloaded = isDownloaded,
        isFavorite = isFavorite,
        isQueued = isQueued,
        isDrown = isDrown,
        hasNewMessages = hasNewMessages,
        imageboard = imageboard.toModel(),
    )

fun BoardThread.toEntity() =
    StoredThread(
        num = num,
        boardId = boardId,
        title = title,
        lastPostNumber = lastPostNumber,
        newMessageAmount = newMessageAmount,
        postCount = postCount,
        fileCount = fileCount,
        lasthit = lasthit,
        lastPostRead = lastPostRead,
        isPinned = isPinned,
        isHidden = isHidden,
        isDownloaded = isDownloaded,
        isFavorite = isFavorite,
        isQueued = isQueued,
        isDrown = isDrown,
        hasNewMessages = hasNewMessages,
        imageboard = imageboard.toEntity(),
    )

package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.converters.JsonRoomConverter
import ru.be_more.orange_forum.domain.model.BoardThread

@Entity(
    tableName = "threads",
    primaryKeys = ["num", "boardId"]
)
@TypeConverters(JsonRoomConverter::class)
data class StoredThread(
    val num: Int,
    val boardId: String,
    val title: String,
    val lastPostNumber: Int,
    val newMessageAmount: Int,
    val postCount: Int,
    val fileCount: Int,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val isQueued: Boolean = false
) {
    constructor(thread: BoardThread): this(
        num = thread.num,
        boardId = thread.boardId,
        title = thread.title,
        lastPostNumber = thread.lastPostNumber,
        newMessageAmount = thread.newMessageAmount,
        postCount = thread.postCount,
        fileCount = thread.fileCount,
        isHidden = thread.isHidden,
        isDownloaded = thread.isDownloaded,
        isFavorite = thread.isFavorite,
        isQueued = thread.isQueued,
    )

    fun toModel() =
        BoardThread(
            num = num,
            posts = emptyList(),
            title = title,
            boardId = boardId,
            lastPostNumber = lastPostNumber,
            newMessageAmount = newMessageAmount,
            postCount = postCount,
            fileCount = fileCount,
            isHidden = isHidden,
            isDownloaded = isDownloaded,
            isFavorite = isFavorite,
            isQueued = isQueued,
        )
}
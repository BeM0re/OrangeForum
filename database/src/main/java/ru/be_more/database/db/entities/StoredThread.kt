package ru.be_more.database.db.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import ru.be_more.database.db.converters.JsonRoomConverter

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
    val lasthit: Long,
    val lastPostRead: Int,
    val isPinned: Boolean,
    val isHidden: Boolean,
    val isDownloaded: Boolean,
    val isFavorite: Boolean,
    val isQueued: Boolean,
    val isDrown: Boolean,
    val hasNewMessages: Boolean,
)
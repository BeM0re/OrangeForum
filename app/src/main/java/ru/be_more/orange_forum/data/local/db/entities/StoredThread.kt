package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.converters.JsonRoomConverter

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
    val posts: List<StoredPost> = listOf(),
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val isQueued: Boolean = false
)
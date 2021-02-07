package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "threads",
    primaryKeys = ["num", "boardId"]
)
data class StoredThread(
    val num: Int,
    val title: String,
    val boardId: String,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val isQueued: Boolean = false
)
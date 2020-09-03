package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "threads",
    foreignKeys = [ForeignKey(entity = StoredBoard::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("boardId"),
        onDelete = ForeignKey.CASCADE)])
data class StoredThread(
    @PrimaryKey val num: Int,
    val title: String,
    val boardId: String,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false
)
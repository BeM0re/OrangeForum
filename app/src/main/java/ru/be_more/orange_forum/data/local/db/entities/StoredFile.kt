package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class StoredFile(
    val boardId: String,
    val postNum: Int,
    val displayName: String = "",
    val height: Int = 0,
    val width: Int = 0,
    val tn_height: Int = 0,
    val tn_width: Int = 0,
    @PrimaryKey val webPath: String,
    val localPath: String = "",
    val webThumbnail: String = "",
    val localThumbnail: String = "",
    val duration : String = "",
    val threadNum: Int = 0,
    val isOpPostFile: Boolean = false
)

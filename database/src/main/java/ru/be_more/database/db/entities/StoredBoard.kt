package ru.be_more.database.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.be_more.database.db.converters.JsonRoomConverter

@Entity(tableName = "boards")
@TypeConverters(JsonRoomConverter::class)
data class StoredBoard(
    @PrimaryKey val id: String,
    val name: String,
    val category: String = "",
    val isFavorite: Boolean,
    val isNameEnabled: Boolean,
    val isTripEnabled: Boolean,
    val isSubjectEnabled: Boolean,
    val isSageEnabled: Boolean,
    val isIconEnabled: Boolean,
    val isFlagEnabled: Boolean,
    val isTagEnabled: Boolean,
    val isPostingEnabled: Boolean,
    val isLikeEnabled: Boolean,
    val fileTypes: List<String>?,
    val maxCommentSize: Int?,
    val maxFileSize: Int?,
    val tags: List<String>?,
    val icons: List<StoredIcon>?,
)
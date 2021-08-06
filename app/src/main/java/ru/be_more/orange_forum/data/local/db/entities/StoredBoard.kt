package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.converters.JsonRoomConverter

@Entity(tableName = "boards")
@TypeConverters(JsonRoomConverter::class)
data class StoredBoard(
    @PrimaryKey val id: String,
    val categoryId: String = "",
    val name: String,
    val threads: List<StoredThread> = listOf(),
    val isFavorite: Boolean = false
)
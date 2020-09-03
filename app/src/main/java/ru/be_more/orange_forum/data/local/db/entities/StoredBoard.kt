package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class StoredBoard(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String,
    val isFavorite: Boolean = false
)
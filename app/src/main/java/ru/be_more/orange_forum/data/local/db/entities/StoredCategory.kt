package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class StoredCategory(
    @PrimaryKey val id: String,
    val name: String
)
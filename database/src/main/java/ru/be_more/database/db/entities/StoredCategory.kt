package ru.be_more.database.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class StoredCategory(
    @PrimaryKey val name: String,
    val isExpanded: Boolean,
    val imageboard: StoredImageboard,
)
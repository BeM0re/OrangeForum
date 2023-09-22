package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.be_more.orange_forum.domain.model.Category

@Entity(tableName = "categories")
data class StoredCategory(
    @PrimaryKey val name: String,
    val isExpanded: Boolean,
) {
    constructor(category: Category): this(
        name = category.name,
        isExpanded = category.isExpanded
    )

    fun toModel(): Category =
        Category(
            name = name,
            boards = emptyList(),
            isExpanded = isExpanded,
        )
}
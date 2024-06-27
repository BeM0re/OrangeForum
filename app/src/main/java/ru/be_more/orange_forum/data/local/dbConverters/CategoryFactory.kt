package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredCategory
import ru.be_more.model.model.Category

object CategoryFactory {

    fun fromEntity(storedBoard: StoredCategory): Category =
        Category(
            name = storedBoard.name,
            boards = emptyList(),
            isExpanded = storedBoard.isExpanded,
        )

    fun toEntity(category: Category) =
        StoredCategory(
            name = category.name,
            isExpanded = category.isExpanded
        )
}
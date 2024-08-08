package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredCategory
import ru.be_more.model.model.Category
import ru.be_more.model.model.ImageboardType

fun StoredCategory.toModel(): Category =
    Category(
        name = name,
        boards = emptyList(),
        isExpanded = isExpanded,
        imageboardType = ImageboardType.valueOf(imageboardType),
    )

fun Category.toEntity() =
    StoredCategory(
        name = name,
        isExpanded = isExpanded,
        imageboardType = imageboardType.name,
    )

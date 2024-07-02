package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredCategory
import ru.be_more.model.model.Category

fun StoredCategory.toModel(): Category =
    Category(
        name = name,
        boards = emptyList(),
        isExpanded = isExpanded,
        imageboard = imageboard.toModel(),
    )

fun Category.toEntity() =
    StoredCategory(
        name = name,
        isExpanded = isExpanded,
        imageboard = imageboard.toEntity(),
    )

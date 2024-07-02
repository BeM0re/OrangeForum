package ru.be_more.orange_forum.data.local.dbConverters

import ru.be_more.database.db.entities.StoredImageboard
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType

fun StoredImageboard.toModel(): Imageboard =
    Imageboard(
        baseUrl = baseUrl,
        attachmentUrl = attachmentUrl,
        staticDataUrl = staticDataUrl,
        type = ImageboardType.valueOf(type)
    )

fun Imageboard.toEntity() =
    StoredImageboard(
        baseUrl = baseUrl,
        attachmentUrl = attachmentUrl,
        staticDataUrl = staticDataUrl,
        type = type.name
    )
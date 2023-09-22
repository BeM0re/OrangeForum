package ru.be_more.orange_forum.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.converters.JsonRoomConverter
import ru.be_more.orange_forum.domain.model.Board

@Entity(tableName = "boards")
@TypeConverters(JsonRoomConverter::class)
data class StoredBoard(
    @PrimaryKey val id: String,
    val name: String,
    val category: String = "",
    val isFavorite: Boolean = false,
    val isExpanded: Boolean = false,
) {
    constructor(board: Board): this(
        id = board.id,
        name = board.name,
        category = board.category,
        isFavorite = board.isFavorite,
        isExpanded = board.isExpanded,
    )

    fun toModel() =
        Board(
            name = name,
            id = id,
            category = category,
            threads = emptyList(),
            isFavorite = isFavorite,
            isExpanded = isExpanded,
        )
}
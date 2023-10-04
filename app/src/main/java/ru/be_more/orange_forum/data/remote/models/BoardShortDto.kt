package ru.be_more.orange_forum.data.remote.models

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.Board

data class BoardShortDto(
    @SerializedName("default_name") val defaultName : String,
    val category : String,
    val id : String,
    val name : String
    //todo icons
) {
    fun toModel(): Board =
        Board(
            name = name,
            id = id,
            category = category,
            isExpanded = false,
        )
}


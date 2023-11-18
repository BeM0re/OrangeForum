package ru.be_more.orange_forum.data.remote.models.dto

import com.google.gson.annotations.SerializedName

data class BoardListDto(
    @SerializedName("boards")
    val boardList : List<BoardShortDto>,
)


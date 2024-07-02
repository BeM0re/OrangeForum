package ru.be_more.network.models.dvach.dto

import com.google.gson.annotations.SerializedName

data class BoardListDto(
    @SerializedName("boards")
    val boardList: List<BoardShortDto>,
)


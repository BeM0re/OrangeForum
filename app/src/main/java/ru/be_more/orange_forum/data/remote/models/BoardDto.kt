package ru.be_more.orange_forum.data.remote.models

data class BoardDto(
    val BoardName : String = "",
    val threads: List<PostDto> = listOf()
)
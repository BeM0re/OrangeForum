package ru.be_more.orange_forum.data.remote.models

data class BoardDto(
    val boardName : String = "",
    val threads: List<PostDto> = listOf()
)
package ru.be_more.orange_forum.data.remote.models

data class ThreadDto(
    val posts_count: Int = 0,
    val title: String = "",
    val threads: List<PostsDto> = listOf(PostsDto())
)
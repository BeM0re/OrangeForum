package ru.be_more.orange_forum.data.remote.models

data class DvachThread(
    val posts_count: Int = 0,
    val title: String = "",
    val threads: List<DvachPosts> = listOf(DvachPosts())
)
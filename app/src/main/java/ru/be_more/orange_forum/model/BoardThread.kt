package ru.be_more.orange_forum.model

data class BoardThread(
    var num : Int,
    var posts: List<Post> = listOf()
)
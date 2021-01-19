package ru.be_more.orange_forum.data.remote.models

data class PostDto(
    val num: Int,
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files: List<FileDto>,
    val files_count: Int,
    val op: Int,
    val posts_count: Int,
    val subject: String,
    val timestamp: Int,
    val number: Int
)
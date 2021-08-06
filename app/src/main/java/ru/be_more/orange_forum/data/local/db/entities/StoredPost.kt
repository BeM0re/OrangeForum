package ru.be_more.orange_forum.data.local.db.entities

import java.util.*

data class StoredPost(
    val num: Int,
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files: List<StoredFile> = listOf(),
    val files_count: Int,
    val op: Int,
    val posts_count: Int,
    val subject: String,
    val timestamp: Int,
    val number: Int, //Порядковый номер в треде
    val replies: Stack<Int> = Stack()
)
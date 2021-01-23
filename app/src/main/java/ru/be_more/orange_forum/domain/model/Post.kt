package ru.be_more.orange_forum.domain.model

import java.util.*

data class Post (
    val num: Int, //ID
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files: List<AttachFile> = listOf(),
    val files_count: Int,
    val op: Int,
    val posts_count: Int,
    val subject: String,
    val timestamp: Int,
    val number: Int, //Порядковый номер в треде
    val replies: Stack<Int> = Stack()
) : ModalContent()
package ru.be_more.orange_forum.domain.model

import java.util.*

data class Post(
    val boardId: String,
    val threadNum: Int,
    val id: Int,
    val name: String,
    val comment: String,
    val isOpPost: Boolean,
    val date: String,
    val email: String,
    val files: List<AttachFile> = listOf(),
    val filesCount: Int,
    val isAuthorOp: Boolean,
    val postsCount: Int,
    val subject: String,
    val timestamp: Int,
    val number: Int, //Порядковый номер в треде
    val replies: Stack<Int> = Stack()
) : ModalContent()
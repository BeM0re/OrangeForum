package ru.be_more.orange_forum.model

import java.util.*

data class Post (
    var num: Int, //ID
    var name: String,
    var comment: String,
    var date: String,
    var email: String,
    var files: List<AttachFile> = listOf(),
    var files_count: Int,
    var op: Int,
    var posts_count: Int,
    var subject: String,
    var timestamp: Int,
    var number: Int, //Порядковый номер в треде
    var replies: Stack<Int> = Stack()
) : ModalContent()
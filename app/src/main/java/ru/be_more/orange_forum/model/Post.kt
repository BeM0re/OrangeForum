package ru.be_more.orange_forum.model

data class Post (
    var num: Int,
    var name: String,
    var comment: String,
    var date: String,
    var email: String,
    var files: List<AttachFile>,
    var files_count: Int,
    var op: Int,
    var posts_count: Int,
    var subject: String,
    var timestamp: Int
)
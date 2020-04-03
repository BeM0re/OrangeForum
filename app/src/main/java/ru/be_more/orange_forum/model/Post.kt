package ru.be_more.orange_forum.model

data class Post (
    var num: Int,
    var comment: String,
    var date: String,
    var email: String,
    var files: List<AttachFile>,
    var files_count: Int,
    var op: Int,
    var post_count: Int,
    var subject: String,
    var timestamp: Int
)
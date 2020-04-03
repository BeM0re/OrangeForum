package ru.be_more.orange_forum.model

data class AttachFile(
    var displayname: String,
    var height: Int,
    var width: Int,
    var tn_height: Int,
    var tn_width: Int,
    var path: String,
    var thumbnail: String
)
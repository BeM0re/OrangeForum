package ru.be_more.orange_forum.model

data class AttachFile(
    var displayName: String = "",
    var height: Int = 0,
    var width: Int = 0,
    var tn_height: Int = 0,
    var tn_width: Int = 0,
    var path: String = "",
    var thumbnail: String = "",
    var duration : String = ""
)
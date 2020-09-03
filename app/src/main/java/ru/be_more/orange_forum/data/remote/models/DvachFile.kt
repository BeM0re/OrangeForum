package ru.be_more.orange_forum.data.remote.models

data class DvachFile(
    val displayname: String,
    val height: Int,
    val width: Int,
    val tn_height: Int,
    val tn_width: Int,
    val path: String,
    val thumbnail: String,
    val duration: String = ""
)
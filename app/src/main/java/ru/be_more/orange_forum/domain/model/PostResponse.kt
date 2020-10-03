package ru.be_more.orange_forum.domain.model

data class PostResponse(
    val error : String,
    val Status :String,
    val Num: Int,
    val Reason: String
)
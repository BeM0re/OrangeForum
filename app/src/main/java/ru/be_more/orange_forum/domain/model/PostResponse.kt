package ru.be_more.orange_forum.domain.model

data class PostResponse(
    val error : String,
    val status :String,
    val num: Int,
    val reason: String
)
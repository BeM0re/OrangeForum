package ru.be_more.orange_forum.data.remote.models

data class DvachBoard(
    val BoardName : String = "",
    val threads: List<DvachPost> = listOf()
)
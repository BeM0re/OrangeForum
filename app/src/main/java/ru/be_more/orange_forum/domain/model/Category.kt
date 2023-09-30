package ru.be_more.orange_forum.domain.model

data class Category(
    val name: String,
    val boards: List<Board>,
    val isExpanded: Boolean,
)

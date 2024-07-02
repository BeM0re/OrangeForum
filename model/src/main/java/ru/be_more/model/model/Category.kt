package ru.be_more.model.model

data class Category(
    val name: String,
    val boards: List<Board>,
    val isExpanded: Boolean,
    val imageboard: Imageboard,
)

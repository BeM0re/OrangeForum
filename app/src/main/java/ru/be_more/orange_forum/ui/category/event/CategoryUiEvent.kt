package ru.be_more.orange_forum.ui.category.event

sealed class CategoryUiEvent {
    data class CategorySelected(var board: String) : CategoryUiEvent()
}
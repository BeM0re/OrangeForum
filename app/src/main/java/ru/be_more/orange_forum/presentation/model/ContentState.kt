package ru.be_more.orange_forum.presentation.model

sealed interface ContentState {
    data object Loading : ContentState
    data object Content : ContentState
    data class Error(val message: String) : ContentState
}
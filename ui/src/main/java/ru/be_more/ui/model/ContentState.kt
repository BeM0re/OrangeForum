package ru.be_more.ui.model

sealed interface ContentState {
    data object Loading : ContentState
    data object Content : ContentState
    data class Error(val message: String) : ContentState
}
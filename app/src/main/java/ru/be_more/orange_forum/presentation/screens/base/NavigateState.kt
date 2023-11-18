package ru.be_more.orange_forum.presentation.screens.base

sealed interface NavigateState {
    data class NavigateToPosting(
        val boardId: String,
        val threadNum: Int,
        val additionalString: String,
    )
    //todo add other args
}
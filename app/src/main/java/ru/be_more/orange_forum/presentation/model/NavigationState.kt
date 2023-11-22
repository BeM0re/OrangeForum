package ru.be_more.orange_forum.presentation.model

sealed interface NavigationState {
    data class NavigateToBoard(
        val boardId: String,
    ) : NavigationState
    data class NavigateToThread(
        val boardId: String,
        val threadNum: Int,
    ) : NavigationState
    data class NavigateToReply(
        val boardId: String,
        val threadNum: Int,
        val additionalString: String,
    ) : NavigationState
    data class NavigateToThreadCreating(
        val boardId: String,
    ) : NavigationState
}
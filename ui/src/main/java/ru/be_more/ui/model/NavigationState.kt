package ru.be_more.ui.model

import ru.be_more.model.model.ImageboardType

sealed interface NavigationState {
    data class NavigateToBoard(
        val imageboardType: ImageboardType,
        val boardId: String,
    ) : NavigationState

    data class NavigateToThread(
        val imageboardType: ImageboardType,
        val boardId: String,
        val threadNum: Int,
    ) : NavigationState

    data class NavigateToReply(
        val imageboardType: ImageboardType,
        val boardId: String,
        val threadNum: Int,
        val additionalString: String,
    ) : NavigationState

    data class NavigateToThreadCreating(
        val imageboardType: ImageboardType,
        val boardId: String,
    ) : NavigationState
}
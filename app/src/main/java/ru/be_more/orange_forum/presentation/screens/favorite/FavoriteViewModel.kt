package ru.be_more.orange_forum.presentation.screens.favorite

import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.composeViews.initArgs.QueueItem
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ShortBoardInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ShortThreadInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class FavoriteViewModel (
    private val favoriteInteractor : InteractorContract.FavoriteInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val prefs: Preferences
): BaseViewModel() {

    var items = MutableStateFlow(listOf<QueueItem>())
        private set

    init {
        subscribeToData()
        refresh()
    }

    private fun refresh() =
        runCoroutine("refresh") {
            favoriteInteractor.updateFavoriteThreadInfo()
        }

    private fun subscribeToData() =
        runCoroutine("subscribeToData") {
            favoriteInteractor
                .getBoardListFlow()
                .collect{ items.emit(prepareItemList(it)) }
        }

    private fun prepareItemList(boards: List<Board>): List<QueueItem> =
        buildList {
            boards.forEach { board ->
                ShortBoardInitArgs(
                    boardId = board.id,
                    boardName = board.name,
                    onClick = ::navigateToBoard
                ).also { add(it) }

                board.threads.map { thread ->
                    ShortThreadInitArgs(
                        boardId = board.id,
                        threadNum = thread.num,
                        title = thread.title,
                        isDrown = thread.isDrown,
                        hasNewMessage = thread.hasNewMessages,
                        onClick = ::navigateToThread
                    )
                }.also { addAll(it) }
            }
        }

    fun removeThread(boardId: String, threadNum: Int) =
        runCoroutine("removeThread") {
            threadInteractor.markFavorite(boardId, threadNum)
        }
}
package ru.be_more.orange_forum.presentation.screens.favorite

import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.model.model.Board
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import ru.be_more.ui.composeViews.initArgs.QueueItem
import ru.be_more.ui.composeViews.initArgs.ShortBoardInitArgs
import ru.be_more.ui.composeViews.initArgs.ShortThreadInitArgs
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(
    private val favoriteInteractor : InteractorContract.FavoriteInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
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
                    onClick = { navigateToBoard(board.imageboardType, it) }
                ).also { add(it) }

                board.threads.map { thread ->
                    ShortThreadInitArgs(
                        boardId = board.id,
                        threadNum = thread.num,
                        title = thread.title,
                        isDrown = thread.isDrown,
                        hasNewMessage = thread.hasNewMessages,
                        onClick = { boardId, threadNum -> navigateToThread(thread.imageboardType, boardId, threadNum) }
                    )
                }.also { addAll(it) }
            }
        }

    fun removeThread(boardId: String, threadNum: Int) =
        runCoroutine("removeThread") {
            threadInteractor.markFavorite(boardId, threadNum)
        }
}
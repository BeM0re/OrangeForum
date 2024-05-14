package ru.be_more.orange_forum.presentation.screens.queue

import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.composeViews.initArgs.QueueItem
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ShortBoardInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ShortThreadInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class QueueViewModel(
    private val queueInteractor: InteractorContract.QueueInteractor,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    private val prefs: Preferences
) : BaseViewModel() {

    var items = MutableStateFlow(listOf<QueueItem>())

    init {
        runCoroutine("init") {
            queueInteractor.getBoardListFlow()
                .collect { items.emit(prepareItemList(it)) }
        }
    }

    private fun prepareItemList(boards: List<Board>): List<QueueItem> =
        buildList {
            boards.forEach { board ->
                ShortBoardInitArgs(
                    boardId = board.id,
                    boardName = board.name,
                    onClick = ::navigateToBoard
                ).also { add(it) }

                board.threads.forEach { thread ->
                    ShortThreadInitArgs(
                        boardId = board.id,
                        threadNum = thread.num,
                        title = thread.title,
                        isDrown = false,
                        hasNewMessage = false,
                        onClick = ::navigateToThread
                    ).also { add(it) }
                }
            }
        }

    fun clear() =
        runCoroutine("clear") {
            queueInteractor.clear()
        }
}
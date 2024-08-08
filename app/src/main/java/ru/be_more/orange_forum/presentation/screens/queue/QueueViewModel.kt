package ru.be_more.orange_forum.presentation.screens.queue

import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.database.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.model.model.Board
import ru.be_more.ui.composeViews.initArgs.QueueItem
import ru.be_more.ui.composeViews.initArgs.ShortBoardInitArgs
import ru.be_more.ui.composeViews.initArgs.ShortThreadInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import javax.inject.Inject

class QueueViewModel @Inject constructor(
    private val queueInteractor: InteractorContract.QueueInteractor,
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
                    onClick = { navigateToBoard(board.imageboardType, it) }
                ).also { add(it) }

                board.threads.forEach { thread ->
                    ShortThreadInitArgs(
                        boardId = board.id,
                        threadNum = thread.num,
                        title = thread.title,
                        isDrown = false,
                        hasNewMessage = false,
                        onClick = { boardId, threadNum -> navigateToThread(thread.imageboardType, boardId, threadNum) }
                    ).also { add(it) }
                }
            }
        }

    fun clear() =
        runCoroutine("clear") {
            queueInteractor.clear()
        }
}
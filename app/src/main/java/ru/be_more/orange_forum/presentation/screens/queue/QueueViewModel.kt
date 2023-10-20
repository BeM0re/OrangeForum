package ru.be_more.orange_forum.presentation.screens.queue

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.data.QueueItem
import ru.be_more.orange_forum.presentation.data.ShortBoardInitArgs
import ru.be_more.orange_forum.presentation.data.ShortThreadInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class QueueViewModel(
    private val queueInteractor: InteractorContract.QueueInteractor,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    private val prefs: Preferences
) : BaseViewModel() {

    var items by mutableStateOf(listOf<QueueItem>())
        private set

    init {
        queueInteractor
            .observe()
            .defaultThreads()
            .subscribe(
                { items = prepareItemList(it) },
                { Log.e("QueueViewModel", "QueueViewModel = $it") }
            )
            .addToSubscribe()
    }

    private fun prepareItemList(boards: List<Board>): List<QueueItem> =
        buildList {
            boards.forEach { board ->
                ShortBoardInitArgs(
                    boardId = board.id,
                    boardName = board.name,
                ).also { add(it) }

                board.threads.forEach { thread ->
                    ShortThreadInitArgs(
                        boardId = board.id,
                        threadNum = thread.num,
                        title = thread.title,
                    ).also { add(it) }
                }
            }
        }

    fun removeThread(boardId: String, threadNum: Int) =
        threadInteractor
            .markQueued(
                boardId = boardId,
                threadNum = threadNum,
            )
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("QueueViewModel", "QueueViewModel.removeThread = $it") }
            )
            .addToSubscribe()

    fun clear() =
        queueInteractor
            .clear()
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("QueueViewModel", "QueueViewModel.clear = $it") }
            )
            .addToSubscribe()
}
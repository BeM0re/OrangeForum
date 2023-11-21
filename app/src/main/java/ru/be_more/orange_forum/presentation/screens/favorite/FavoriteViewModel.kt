package ru.be_more.orange_forum.presentation.screens.favorite

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var items by mutableStateOf(listOf<QueueItem>())
        private set

    init {
        subscribeToData()
        refresh()
    }

    private fun refresh() {
        favoriteInteractor
            .updateFavoriteThreadInfo()
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("FavoriteViewModel", "FavoriteViewModel.init.observe = $it") }
            )
            .addToSubscribe()
    }

    private fun subscribeToData() {
        favoriteInteractor
            .observe()
            .defaultThreads()
            .subscribe(
                { items = prepareItemList(it) },
                { Log.e("FavoriteViewModel", "FavoriteViewModel.init.observe = $it") }
            )
            .addToSubscribe()
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
        threadInteractor.markFavorite(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                {  },
                { Log.e("M_DownFavViewModelImpl","removing from queue error = $it")}
            )
            .addToSubscribe()
}
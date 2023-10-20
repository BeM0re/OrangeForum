package ru.be_more.orange_forum.presentation.screens.board

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.presentation.data.ListItemArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseModalContentViewModel

class BoardViewModel(
    override val boardId: String,
    private val boardInteractor: InteractorContract.BoardInteractor,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    override val postInteractor: InteractorContract.PostInteractor,
    private val prefs: Preferences,
): BaseModalContentViewModel(
    boardId = boardId,
    postInteractor = postInteractor,
) {

    var items by mutableStateOf(listOf<ListItemArgs>())
        private set

    var screenTitle by mutableStateOf("")
        private set

    var isFavorite by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        boardInteractor
            .observe(boardId)
            .defaultThreads()
            .doOnSubscribe { isLoading = true }
            .subscribe(
                { board ->
                    screenTitle = board.name
                    isFavorite = board.isFavorite
                    items = prepareItemList(board.threads)
                    isLoading = false
                },
                { Log.e("BoardViewModel", "BoardViewModel.init: \n $it") }
            )
            .addToSubscribe()
    }

    private fun prepareItemList(threads: List<BoardThread>): List<ListItemArgs> =
        threads.mapNotNull { thread ->
            val post = thread.posts.getOrNull(0) ?: return@mapNotNull null

            if (thread.isHidden)
                HiddenOpPostInitArgs(
                    post = post,
                    onClick = ::hideThread
                )
            else
                OpPostInitArgs(
                    post = post,
                    isQueued = thread.isQueued,
                    onHide = ::hideThread,
                    onQueue = ::addToQueue,
                    onPic = ::onPicClicked,
                    onTextLinkClick = ::onTextLinkClicked,
                )
        }

    private fun addToQueue(boardId: String, threadNum: Int) {
        threadInteractor
            .markQueued(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { prefs.queueToUpdate = true },
                { Log.e("BoardViewModel","BoardViewModel.addToQueue: \n $it") }
            )
            .addToSubscribe()
    }

    private fun hideThread(boardId: String, threadNum: Int) {
        threadInteractor
            .markHidden(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("BoardViewModel","BoardViewModel.hideThread: \n $it") }
            )
            .addToSubscribe()
    }

    fun setFavorite() {
        boardInteractor
            .markFavorite(boardId)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("BoardViewModel","BoardViewModel.setFavorite: \n $it") }
            )
            .addToSubscribe()
    }

    fun refresh() {
        boardInteractor
            .refresh(boardId)
            .doOnSubscribe { isLoading = true }
            .subscribe(
                { isLoading = false },
                { Log.e("BoardViewModel", "BoardViewModel.init: \n $it") }
            )
            .addToSubscribe()
    }
}
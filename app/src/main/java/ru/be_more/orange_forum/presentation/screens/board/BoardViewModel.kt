package ru.be_more.orange_forum.presentation.screens.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.model.model.Board
import ru.be_more.model.model.BoardSetting
import ru.be_more.model.model.BoardThread
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import ru.be_more.ui.composeViews.initArgs.HiddenOpPostInitArgs
import ru.be_more.ui.composeViews.initArgs.OpPostInitArgs
import ru.be_more.ui.composeViews.initArgs.ListItemArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseModalContentViewModel

class BoardViewModel(
    val imageboardType: ImageboardType,
    override val boardId: String,
    private val boardInteractor: InteractorContract.BoardInteractor,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    override val postInteractor: InteractorContract.PostInteractor,
    override val replyInteractor: InteractorContract.ReplyInteractor,
//    private val prefs: Preferences,
): BaseModalContentViewModel(
    boardId = boardId,
    postInteractor = postInteractor,
    replyInteractor = replyInteractor,
) {

    override val boardSetting: BoardSetting
        get() = board.boardSetting

    private lateinit var board: Board

    var items = MutableStateFlow(listOf<ListItemArgs>())

    var screenTitle = MutableStateFlow("")

    var isFavorite = MutableStateFlow(false)

    var isLoading = MutableStateFlow(false)

    init {
        runCoroutine("init.getFlow") {
            isLoading.emit(true)

            boardInteractor
                .getBoardFlow(boardId)
                .collect {board ->
                    this@BoardViewModel.board = board
                    screenTitle.emit(board.name)
                    isFavorite.emit(board.isFavorite)
                    items.emit(prepareItemList(board.threads))
                    isLoading.emit(false)
                }
        }

        refresh()
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
                    onTextLinkClick = { onTextLinkClicked(imageboardType, it) },
                    onClick = { boardId, threadNum -> navigateToThread(imageboardType, boardId, threadNum) },
                    onPostNumClick = { replyToPost(imageboardType, it) }
                )
        }

    private fun addToQueue(boardId: String, threadNum: Int) =
        runCoroutine {
            threadInteractor.markQueued(boardId, threadNum)
        }

    private fun hideThread(boardId: String, threadNum: Int) =
        runCoroutine {
            threadInteractor.markHidden(boardId, threadNum)
        }

    fun setFavorite() =
        runCoroutine {
            boardInteractor.markFavorite(boardId)
        }

    fun refresh() =
        runCoroutine("refresh") {
            isLoading.emit(true)
            showLoading()
            boardInteractor.refresh(imageboardType, boardId)
            isLoading.emit(false)
            showContent()
        }

    fun search(query: String) =
        runCoroutine {
            boardInteractor.search(query)
        }

    fun onNewThreadClicked() =
        navigateToThreadCreating(imageboardType, boardId)

    class Factory @AssistedInject constructor(
        @Assisted("imageboard") private val imageboardType: String,
        @Assisted("boardId") private val boardId: String,
        private val boardInteractor: InteractorContract.BoardInteractor,
        private val threadInteractor: InteractorContract.ThreadInteractor,
        private val postInteractor: InteractorContract.PostInteractor,
        private val replyInteractor: InteractorContract.ReplyInteractor,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == BoardViewModel::class.java)

            return BoardViewModel(
                imageboardType = ImageboardType.valueOf(imageboardType),
                boardId = boardId,
                boardInteractor = boardInteractor,
                threadInteractor = threadInteractor,
                postInteractor = postInteractor,
                replyInteractor = replyInteractor,
            ) as T
        }

        @AssistedFactory
        interface AFactory {
            fun create(
                @Assisted("imageboard") imageboardType: String,
                @Assisted("boardId") boardId: String,
            ): Factory
        }
    }
}
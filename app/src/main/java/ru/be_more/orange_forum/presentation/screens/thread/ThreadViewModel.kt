package ru.be_more.orange_forum.presentation.screens.thread

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.BoardSetting
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.composeViews.initArgs.PostInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseModalContentViewModel

class ThreadViewModel(
    override val boardId: String,
    private val threadNum: Int,
    private val boardInteractor: InteractorContract.BoardInteractor,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    override val postInteractor: InteractorContract.PostInteractor,
    override val replyInteractor: InteractorContract.ReplyInteractor,
    private val prefs: Preferences
) : BaseModalContentViewModel(
    boardId = boardId,
    postInteractor = postInteractor,
    replyInteractor = replyInteractor,
) {

    override val boardSetting: BoardSetting
        get() = settings

    private var lastPostViewedJob: Job? = null
    private lateinit var settings: BoardSetting

    var screenTitle = MutableStateFlow("")
    var isFavorite = MutableStateFlow(false)
    var isQueued = MutableStateFlow(false)
    var isDownloaded = MutableStateFlow(false)
    var items = MutableStateFlow(listOf<PostInitArgs>())

    private val scrollToItemNumMutableFlow = MutableSharedFlow<Int>()
    val scrollToItemNumFlow = scrollToItemNumMutableFlow.asSharedFlow()

    init {
        runCoroutine("init") {
            settings = requireNotNull(getBoard().await())
                .boardSetting

            threadInteractor.refresh(boardId, threadNum)

            threadInteractor.getBoardFlow(boardId, threadNum)
                .collect { thread ->
                    screenTitle.emit(thread.title)
                    isFavorite.emit(thread.isFavorite)
                    isQueued.emit(thread.isQueued)
                    isDownloaded.emit(thread.isDownloaded)
                    items.emit(prepareItemList(thread.posts))
                    scrollToPost(
                        postNum = items.value
                            .indexOfFirst { it.post.id == thread.lastPostRead }
                            .takeIf { it >= 0 }
                            ?: 0
                    )
                    showContent()
                }
        }
    }

    private suspend fun getBoard() =
        viewModelScope.async {
            boardInteractor
                .getBoard(boardId)
        }

    private fun prepareItemList(posts: List<Post>): List<PostInitArgs> =
        posts.map { post ->
            PostInitArgs(
                post = post,
                onPicClick = ::onPicClicked,
                onTextLinkClick = ::onTextLinkClicked,
                onPostNumClick = ::replyToPost
            )
        }

    private fun scrollToPost(postNum: Int) {
        viewModelScope.launch {
            scrollToItemNumMutableFlow.emit(postNum)
        }
    }

    fun setFavorite() =
        runCoroutine("setFavorite") {
            threadInteractor.markFavorite(boardId, threadNum)
        }

    fun setQueued() =
        runCoroutine("setQueued") {
            threadInteractor.markQueued(boardId, threadNum)
        }

    fun download() =
        runCoroutine("download") {
            threadInteractor.save(boardId, threadNum)
        }

    fun onReplyClicked() =
        navigateToReply(
            boardId = boardId,
            threadNum = threadNum,
            additionalString = "",
        )

    fun lastPostViewed(postListNum: Int) {
        lastPostViewedJob?.cancel()
        lastPostViewedJob =
        runCoroutine("lastPostViewed") {
            threadInteractor.updateLastPostViewed(
                boardId, threadNum, items.value[postListNum].post.id
            )
        }
    }
}
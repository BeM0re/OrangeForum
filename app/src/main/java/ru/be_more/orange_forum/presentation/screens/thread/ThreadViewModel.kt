package ru.be_more.orange_forum.presentation.screens.thread

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.BoardSetting
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.composeViews.initArgs.PostInitArgs
import ru.be_more.orange_forum.presentation.model.NavigationState
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

    private var lastPostViewedSubscription: Disposable? = null
    private lateinit var settings: BoardSetting

    var screenTitle by mutableStateOf("")
        private set

    var isFavorite by mutableStateOf(false)
        private set

    var isQueued by mutableStateOf(false)
        private set

    var isDownloaded by mutableStateOf(false)
        private set

    var items by mutableStateOf(listOf<PostInitArgs>())
        private set

    private val scrollToItemNumMutableFlow = MutableSharedFlow<Int>()
    val scrollToItemNumFlow = scrollToItemNumMutableFlow.asSharedFlow()

    init {
        boardInteractor
            .getSingle(boardId)
            .doOnSuccess { settings = it.boardSetting }
            .flatMapCompletable {
                threadInteractor.refresh(boardId, threadNum)
            }
            .andThen(
                threadInteractor.observe(boardId, threadNum)
            )
            .defaultThreads()
            .doOnSubscribe { showLoading() }
            .subscribe(
                { thread ->
                    screenTitle = thread.title
                    isFavorite = thread.isFavorite
                    isQueued = thread.isQueued
                    isDownloaded = thread.isDownloaded
                    items = prepareItemList(thread.posts)
                    scrollToPost(
                        postNum = items
                            .indexOfFirst { it.post.id == thread.lastPostRead }
                            .takeIf { it >= 0 }
                            ?: 0
                    )
                    showContent()
                },
                { Log.e("ThreadViewModel", "ThreadViewModel.init: \n $it") }
            )
            .addToSubscribe()
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
        threadInteractor
            .markFavorite(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("ThreadViewModel","ThreadViewModel.setFavorite: \n $it") }
            )
            .addToSubscribe()

    fun setQueued() =
        threadInteractor
            .markQueued(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("ThreadViewModel","ThreadViewModel.setQueued: \n $it") }
            )
            .addToSubscribe()

    fun download() =
        threadInteractor
            .save(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("ThreadViewModel","ThreadViewModel.download: \n $it") }
            )
            .addToSubscribe()

    fun onReplyClicked() =
        navigateToReply(
            boardId = boardId,
            threadNum = threadNum,
            additionalString = "",
        )

    fun lastPostViewed(postListNum: Int) {
        lastPostViewedSubscription?.dispose()
        lastPostViewedSubscription = threadInteractor
            .updateLastPostViewed(
                boardId, threadNum, items[postListNum].post.id
            )
            .defaultThreads()
            .subscribe(
                {},
                { Log.e("ThreadViewModel", "ThreadViewModel.lastPostViewed: \n $it") }
            )
            .also { it.addToSubscribe() }
    }
}
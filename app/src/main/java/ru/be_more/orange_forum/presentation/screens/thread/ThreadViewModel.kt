package ru.be_more.orange_forum.presentation.screens.thread

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.data.PostInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseModalContentViewModel
import java.util.*

class ThreadViewModel(
    override val boardId: String,
    private val threadNum: Int,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    override val postInteractor: InteractorContract.PostInteractor,
    override val replyInteractor: InteractorContract.ReplyInteractor,
    private val prefs: Preferences
) : BaseModalContentViewModel(
    boardId = boardId,
    postInteractor = postInteractor,
    replyInteractor = replyInteractor,
) {

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

    init {
        threadInteractor
            .observe(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { thread ->
                    screenTitle = thread.title
                    isFavorite = thread.isFavorite
                    isQueued = thread.isQueued
                    isDownloaded = thread.isDownloaded
                    items = prepareItemList(thread.posts)
                },
                { Log.e("ThreadViewModel", "ThreadViewModel.init: \n $it") }
            )
            .addToSubscribe()
    }

    override fun getCapture() =
        replyInteractor.getCapture(boardId, threadNum)

    private fun prepareItemList(posts: List<Post>): List<PostInitArgs> =
        posts.map { post ->
            PostInitArgs(
                post = post,
                onPicClick = ::onPicClicked,
                onTextLinkClick = ::onTextLinkClicked,
            )
        }

    fun setFavorite() {
        threadInteractor
            .markFavorite(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("ThreadViewModel","ThreadViewModel.setFavorite: \n $it") }
            )
            .addToSubscribe()
    }

    fun setQueued() {
        threadInteractor
            .markQueued(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("ThreadViewModel","ThreadViewModel.setQueued: \n $it") }
            )
            .addToSubscribe()
    }

    fun download() {
        threadInteractor
            .save(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("ThreadViewModel","ThreadViewModel.download: \n $it") }
            )
            .addToSubscribe()
    }
    //todo new thread
}
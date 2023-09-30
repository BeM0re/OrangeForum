package ru.be_more.orange_forum.presentation.screens.thread

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import java.util.*

//TODO прятать fab при нажатии на ответ
class ThreadViewModel(
    private val boardId: String,
    private val threadNum: Int,
    private val threadInteractor: InteractorContract.ThreadInteractor,
    private val postInteractor: InteractorContract.PostInteractor,
    private val prefs: Preferences
) : BaseViewModel() {

    val post = MutableLiveData<Post>()
    val attachment = MutableLiveData<Attachment>()
    val emptyStack = MutableLiveData<Boolean>()
    val thread = MutableLiveData<BoardThread>()
    val savedPosition = MutableLiveData<Int>()
    val isFavorite = MutableLiveData<Boolean>()
    val isQueued = MutableLiveData<Boolean>()
    val isDownload = MutableLiveData<Boolean>()
    val isRefreshing = MutableLiveData<Boolean>()

    private var boardName: String = ""
    private val modalStack: Stack<ModalContent> = Stack()

    fun init(boardId: String?, threadNum: Int, boardName: String) {

        //если борда и тред не изменились, то данные не перезагружаем
        if (this.boardId != boardId || this.threadNum != threadNum) {
            if (!boardId.isNullOrEmpty()) {
//                this.boardId = boardId
//                this.threadNum = threadNum
                this.boardName = boardName
                refreshThread(false)
            }
        } else {
            thread.postValue(thread.value)
            savedPosition.postValue(savedPosition.value)
            isFavorite.postValue(isFavorite.value)
            isQueued.postValue(isQueued.value)
            isDownload.postValue(isDownload.value)
        }
    }

    fun clearStack() {
        this.modalStack.clear()
    }

    fun putContentInStack(modal: ModalContent) {
        this.modalStack.push(modal)
    }

    fun onBackPressed() {
        if (!modalStack.empty()) {
            modalStack.pop()

            if (!modalStack.empty()) {
                when (val content = modalStack.peek()) {
                    is Attachment -> attachment.postValue(content)
                    is Post -> post.postValue(content)
                }
            } else
                emptyStack.postValue(true)
        }
    }

    fun getSinglePost(postNum: Int) {
        getSinglePost(this.boardId, postNum)
    }

    @SuppressLint("CheckResult")
    fun getSinglePost(boardId: String, postNum: Int) {
        postInteractor.getPost(boardId, postNum)
            .defaultThreads()
            .subscribe(
                {
//                    this.putContentInStack(it) //todo
//                    post.postValue(it)
                },
                { error.postValue("Пост не найден") }
            )
    }

    fun getPost(chanLink: Triple<String, Int, Int>?) {
        if (chanLink == null)
            return

        val post = thread.value?.posts?.find { it.id == chanLink.third }

        if (post != null) {
            putContentInStack(post)
            this.post.postValue(post)
        } else
            getSinglePost(chanLink.first, chanLink.third)
    }

    fun getPost(postNum: Int) {
        val post = thread.value?.posts?.find { it.id == postNum }

        if (post != null) {
            putContentInStack(post)
            this.post.postValue(post)
        } else
            getSinglePost(postNum)
    }

    fun addToQueue(isQueued: Boolean) {
        if (thread.value != null)
            threadInteractor
                .markQueued(
                    boardId = boardId,
                    boardName = boardName,
                    threadNum = threadNum,
                )
                .defaultThreads()
                .subscribe(
                    {
                        this.isQueued.postValue(true)
                        prefs.queueToUpdate = true
                    },
                    { Log.e("M_ThreadViewModelImpl", "Adding in queue error = $it") }
                )
                .addToSubscribe()
    }

    fun setFavorite(isFavorite: Boolean) {
        if (thread.value != null)
            threadInteractor
                .markFavorite(
                    boardId = boardId,
                    boardName = boardName,
                    threadNum = threadNum,
                )
                .defaultThreads()
                .subscribe(
                    {
                        this.isFavorite.postValue(true)
                        prefs.favsToUpdate = true
                    },
                    { Log.e("M_ThreadViewModelImpl", "Adding fav error = $it") }
                )
                .addToSubscribe()
    }

    fun download(isDownload: Boolean) {
        if (thread.value != null)
            if (isDownload)
                threadInteractor
                    .observe(
                        boardId = boardId,
                        threadNum = threadNum
                    )
                    .defaultThreads()
                    .subscribe(
                        { this.isDownload.postValue(true) },
                        { Log.e("M_ThreadViewModelImpl", "Downloading error = $it") }
                    )
                    .addToSubscribe()
            else
                threadInteractor
                    .delete(boardId, threadNum)
                    .defaultThreads()
                    .subscribe(
                        { this.isDownload.postValue(false) },
                        { Log.e("M_ThreadViewModelImpl", "Removing download error = $it") }
                    )
                    .addToSubscribe()
    }

    fun onMenuReady() {
        isFavorite.postValue(isFavorite.value)
        isQueued.postValue(isQueued.value)
        isDownload.postValue(isDownload.value)
    }

    fun closeModal() {
        clearStack()
        onBackPressed()
    }

    fun prepareModal(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {
        if (!fullPicUrl.isNullOrEmpty() || fullPicUri != null)
            Attachment(fullPicUrl, duration, fullPicUri)
                .also {
                    putContentInStack(it)
                    attachment.postValue(it)
                }
    }

    fun onRefresh() {
        refreshThread(true)
    }

    private fun refreshThread(forceUpdate: Boolean) {
        threadInteractor.observe(boardId, threadNum)
            .defaultThreads()
            .doFinally { isRefreshing.postValue(false) }
            .doOnNext {
                thread.postValue(it)
                isDownload.postValue(it.isDownloaded)
                isFavorite.postValue(it.isFavorite)
                isQueued.postValue(it.isQueued)
            }
            .observeOn(Schedulers.io())
            .flatMapCompletable {
                threadInteractor.updateLastPostNum(boardId, threadNum, it.posts.last().id)
            }
            .andThen(threadInteractor.updateNewMessages(boardId, threadNum, 0))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                { Log.e("M_ThreadPresenter", "get tread in tread presenter error = $it") }
            )
            .addToSubscribe()
    }
}
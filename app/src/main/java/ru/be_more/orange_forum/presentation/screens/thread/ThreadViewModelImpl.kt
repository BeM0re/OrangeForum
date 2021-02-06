package ru.be_more.orange_forum.presentation.screens.thread

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.PresentationContract
import java.util.*

//TODO прятать fab при нажатии на ответ
class ThreadViewModelImpl (
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences
): PresentationContract.ThreadViewModel{
    
    override val post = MutableLiveData<Post>()
    override val attachment = MutableLiveData<Attachment>()
    override val emptyStack = MutableLiveData<Boolean>()
    override val thread = MutableLiveData<BoardThread>()
    override val savedPosition = MutableLiveData<Int>()
    override val isFavorite = MutableLiveData<Boolean>()
    override val isQueued = MutableLiveData<Boolean>()
    override val isDownload = MutableLiveData<Boolean>()

    private var boardId: String = ""
    private var boardName: String = ""
    private var threadNum: Int = 0
    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    override fun init(boardId: String?, threadNum: Int, boardName: String){

        //если борда и тред не изменились, то данные не перезагружаем
        if (this.boardId != boardId || this.threadNum != threadNum) {
            if (!boardId.isNullOrEmpty()) {
                this.boardId = boardId
                this.threadNum = threadNum
                this.boardName = boardName

                threadInteractor.getThread(boardId, threadNum)
                    .subscribe(
                        {
                            thread.postValue(it)
                            isDownload.postValue(it.isDownloaded)
                            isFavorite.postValue(it.isFavorite)
                            isQueued.postValue(it.isQueued)
                        },
                        { Log.e("M_ThreadPresenter", "get tread in tread presenter error = $it") }
                    )
            }
        }
        else{
            thread.postValue(thread.value)
            savedPosition.postValue(savedPosition.value)
            isFavorite.postValue(isFavorite.value)
            isQueued.postValue(isQueued.value)
            isDownload.postValue(isDownload.value)
        }
    }

    override fun onDestroy() {

    }

    override fun clearStack() {
        this.modalStack.clear()
    }

    override fun putContentInStack(modal: ModalContent) {
        this.modalStack.push(modal)
    }

    override fun onBackPressed() {
        modalStack.pop()

        if(!modalStack.empty()) {
            when(val content = modalStack.peek()){
                is Attachment -> attachment.postValue(content)
                is Post -> post.postValue(content)
            }
        } else
            emptyStack.postValue(true)
    }

    override fun getSinglePost(postNum: Int) {
        getSinglePost(this.boardId, postNum)
    }

    @SuppressLint("CheckResult")
    override fun getSinglePost(boardId: String, postNum: Int){
        postInteractor.getPost(boardId, postNum)
            .subscribe(
                {
                    this.putContentInStack(it)
                    post.postValue(it)
                },
                { App.showToast("Пост не найден") }
            )
    }

    override fun getPost(chanLink: Triple<String, Int, Int>) {
        val post = thread.value?.posts?.find { it.num == chanLink.third }

        if (post != null) {
            putContentInStack(post)
            this.post.postValue(post)
        }
        else
            getSinglePost(chanLink.first, chanLink.third)
    }

    override fun getPost(postNum: Int) {
        val post = thread.value?.posts?.find { it.num == postNum }

        if (post != null) {
            putContentInStack(post)
            this.post.postValue(post)
        }
        else
            getSinglePost(postNum)
    }

    override fun addToQueue(isQueued: Boolean) {
        if (thread.value != null)
            if (!isQueued)
                threadInteractor
                    .addThreadToQueue(threadNum, boardId, boardName)
                    .subscribe(
                        {
                            this.isQueued.postValue(true)
                            prefs.queueToUpdate = true
                        },
                        { Log.e("M_ThreadViewModelImpl","Adding in queue error = $it") }
                    )
            else
                threadInteractor
                    .removeThreadFromQueue(boardId, threadNum)
                    .subscribe(
                        {
                            this.isQueued.postValue(false)
                            prefs.queueToUpdate = true
                        },
                        { Log.e("M_ThreadViewModelImpl","Removing from queue error = $it") }
                    )
    }

    override fun setFavorite(isFavorite: Boolean) {
        if (thread.value != null)
            if (isFavorite)
                threadInteractor
                    .addThreadToFavorite(threadNum, boardId, boardName)
                    .subscribe(
                        {
                            this.isFavorite.postValue(true)
                            prefs.favsToUpdate = true
                        },
                        { Log.e("M_ThreadViewModelImpl","Adding fav error = $it") }
                    )
            else
                threadInteractor
                    .removeThreadFromFavorite(boardId, threadNum)
                    .subscribe(
                        {
                            this.isFavorite.postValue(false)
                            prefs.favsToUpdate = true
                        },
                        { Log.e("M_ThreadViewModelImpl","Removing fav error = $it") }
                    )
    }

    override fun download(isDownload: Boolean) {
        if (thread.value != null)
            if (isDownload)
                threadInteractor
                    .downloadThread(threadNum, boardId, boardName)
                    .subscribe(
                        {
                            this.isDownload.postValue(true)
                        },
                        {
                            Log.e("M_ThreadViewModelImpl","Downloading error = $it")
                        }
                    )
            else
                threadInteractor
                    .deleteThread(boardId, threadNum)
                    .subscribe(
                            {
                                this.isDownload.postValue(false)
                            },
                        {
                            Log.e("M_ThreadViewModelImpl","Removing download error = $it")
                        }
                    )
    }

    override fun onMenuReady() {
        isFavorite.postValue(isFavorite.value)
        isQueued.postValue(isQueued.value)
        isDownload.postValue(isDownload.value)
    }

}
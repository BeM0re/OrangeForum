package ru.be_more.orange_forum.ui.thread

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.ui.PresentationContract
import java.util.*

//TODO прятать fab при нажатии на ответ
class ThreadViewModelImpl (
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor
): PresentationContract.ThreadViewModel{
    
    override val post = MutableLiveData<Post>()
    override val attachment = MutableLiveData<Attachment>()
    override val emptyStack = MutableLiveData<Boolean>()
    override val thread = MutableLiveData<BoardThread>()
    override val savedPosition = MutableLiveData<Int>()

    private var boardId: String = ""
    private var threadNum: Int = 0
    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    override fun init(boardId: String?, threadNum: Int){

        //если борда и тред не изменились, то данные не перезагружаем
        if (this.boardId != boardId || this.threadNum != threadNum) {
            if (!boardId.isNullOrEmpty()) {
                this.boardId = boardId
                this.threadNum = threadNum

                threadInteractor.getThread(boardId, threadNum)
                    .subscribe(
                        {
                            thread.postValue(it)
                        },
                        {
                            Log.d("M_ThreadPresenter", "get tread in tread presenter error = $it")
                        }
                    )
            }
        }
        else{
            thread.postValue(thread.value)
            savedPosition.postValue(savedPosition.value)
        }
    }

    override fun onDestroy() {
        threadInteractor.release()
        postInteractor.release()
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

}
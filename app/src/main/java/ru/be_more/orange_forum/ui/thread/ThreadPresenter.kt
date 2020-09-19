package ru.be_more.orange_forum.ui.thread

import android.annotation.SuppressLint
import android.util.Log
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import java.util.*

class ThreadPresenter (
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private var viewState: ThreadView?
){

    private var adapter : ThreadAdapter? = null

    lateinit var thread: BoardThread
    private lateinit var boardId :String
    private var threadNum: Int = 0 //FIXME get rid of threadNum

    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    fun init(boardId: String, threadNum: Int){

        this.boardId = boardId
        this.threadNum = threadNum

        threadInteractor.getThread(boardId, threadNum)
            .subscribe(
                {
                    thread = it
                    viewState?.loadThread(thread)
                    viewState?.setThreadMarks(thread.isDownloaded, thread.isFavorite)
                },
                {
                    Log.d("M_ThreadPresenter", "get tread in tread presenter error = $it")
                }
            )

    //TODO прятать fab при нажатии на ответ
    }

    fun onDestroy() {
        threadInteractor.release()
        postInteractor.release()
        viewState = null
        adapter = null
    }

    fun initAdapter(thread: BoardThread,
                    picListener: PicOnClickListener,
                    linkListener: LinkOnClickListener) {
        adapter = ThreadAdapter(thread, picListener, linkListener)
    }

    fun getAdapter(): ThreadAdapter? = this.adapter

    fun getBoardId(): String = this.boardId

    fun setBoardId(): String = this.boardId

    fun clearStack() {
        this.modalStack.clear()
    }

    fun putContentInStack(modal: ModalContent) {
        this.modalStack.push(modal)
    }

    fun onBackPressed() {
        modalStack.pop()

        if(!modalStack.empty()) {

            when(val content = modalStack.peek()){
                is Attachment -> viewState?.showPic(content)
                is Post -> viewState?.showPost(content)
            }
        } else
            viewState?.hideModal()
    }

    fun getSinglePost(postNum: Int) {
        getSinglePost(this.boardId, postNum)
    }

    @SuppressLint("CheckResult")
    fun getSinglePost(boardId: String, postNum: Int){
        postInteractor.getPost(boardId, postNum)
            .subscribe(
                {
                    this.putContentInStack(it)
                    viewState?.showPost(it)
                },
                { App.showToast("Пост не найден") }
            )
    }

    fun setThreadMarks(){
        viewState?.setThreadMarks(thread.isDownloaded, thread.isFavorite)
    }
}
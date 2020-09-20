package ru.be_more.orange_forum.ui.download

import android.annotation.SuppressLint
import android.util.Log
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import java.util.*

class DownloadPresenter (
    private val downloadInteractor : InteractorContract.DownloadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private var viewState: DownloadView?
) {

    private val modalStack: Stack<ModalContent> = Stack()
    private lateinit var boards : List<Board>

    @SuppressLint("CheckResult")
    fun initPresenter(){
        downloadInteractor.getDownloads()
            .subscribe({ boards ->
                this.boards = boards
                viewState?.loadDownloads()
            },
                { Log.d("M_DownloadPresenter", "Presenter on first view attach error = $it") }
            )
    }

    fun onDestroy() {
        downloadInteractor.release()
        postInteractor.release()
        viewState = null
    }

    fun putContentInStack(content: ModalContent) {
        this.modalStack.push(content)
    }

    @SuppressLint("CheckResult")
    fun getSinglePost(boardId: String, postNum: Int){
        postInteractor.getPost(boardId, postNum)
            .subscribe(
                {
                    this.putContentInStack(it)
                    viewState?.showPost(it)
                },
                {  App.showToast("Пост не найден" ) }
            )
    }

    fun clearStack() {
        this.modalStack.clear()
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

    fun getBoards(): List<Board> = this.boards

    fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor.deleteThread(boardId, threadNum)
    }
}
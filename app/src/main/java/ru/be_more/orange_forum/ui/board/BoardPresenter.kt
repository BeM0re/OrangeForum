package ru.be_more.orange_forum.ui.board

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
//import moxy.InjectViewState
//import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.*
import ru.be_more.orange_forum.ui.download.DownloadView
import java.util.*
//import javax.inject.Inject

//@InjectViewState
class BoardPresenter /*@Inject constructor*/(
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val viewState: BoardView
)/*: MvpPresenter<BoardView>() */{

    private var board :Board = Board("", "", listOf(), false)
    private var boardId: String = "" //FIXME убрать борд айди, раз есть борда (выше)
    var listener: ((threadNum: Int, threadTitle: String) -> Unit)? = null

    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    fun init(boardId: String, listener: ((threadNum: Int, threadTitle: String) -> Unit)?){

        if (listener!=null)
            this.listener = listener

        if (!boardId.isNullOrEmpty())
            this.boardId = boardId

        boardInteractor.getBoard(this.boardId)
            .subscribe(
                { board ->
                    viewState.loadBoard(board)
                    viewState.setBoardMarks(board.isFavorite)
                },
                {
                    Log.e("M_BoardPresenter","Getting board error = $it")
                }
            )
    }

    /*override*/ fun onDestroy() {
        boardInteractor.release()
        threadInteractor.release()
        postInteractor.release()
//        super.onDestroy()
    }

    fun clearStack() {
        this.modalStack.clear()
    }

    fun putContentInStack(content: ModalContent) {
        this.modalStack.push(content)
    }

    fun onBackPressed() {
        modalStack.pop()

        if(!modalStack.empty()) {
            when(val content = modalStack.peek()){
                is Attachment -> viewState.showPic(content)
                is Post -> viewState.showPost(content)
            }
        } else
            viewState.hideModal()
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
                    viewState.showPost(it)
                },
                { App.showToast("Пост не найден") }
            )
    }

    @SuppressLint("CheckResult")
    fun hideThread(threadNum: Int, isHidden: Boolean) {
        if (!isHidden)
            threadInteractor.markThreadHidden(boardId, board.name, threadNum)
                .subscribe()
        else {
            threadInteractor.unmarkThreadHidden(boardId, threadNum)
                .subscribe()
        }
    }

    fun setBoardMarks(){
        viewState.setBoardMarks(board.isFavorite)
    }

}
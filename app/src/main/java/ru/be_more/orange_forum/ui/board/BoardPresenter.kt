package ru.be_more.orange_forum.ui.board

import android.annotation.SuppressLint
import android.util.Log
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.*
import java.util.*

class BoardPresenter (
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private var viewState: BoardView?)
{

    private var board :Board = Board("", "", listOf(), false)
//    private var boardId: String = "" //FIXME убрать борд айди, раз есть борда (выше)
//    var listener: ((threadNum: Int, threadTitle: String) -> Unit)? = null

    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    fun init(boardId: String?){

//        if (listener!=null)
//            this.listener = listener

        if (!boardId.isNullOrEmpty()) {
            this.board.id = boardId
            boardInteractor.getBoard(this.board.id)
                .subscribe(
                    { board ->
                        this.board = board
                        viewState?.loadBoard(this.board)
                        viewState?.setBoardMarks(this.board.isFavorite)
                    },
                    {
                        Log.e("M_BoardPresenter", "Getting board error = $it")
                    }
                )
        }
    }

    fun onDestroy() {
        boardInteractor.release()
        threadInteractor.release()
        postInteractor.release()
        viewState = null
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
                is Attachment -> viewState?.showPic(content)
                is Post -> viewState?.showPost(content)
            }
        } else
            viewState?.hideModal()
    }

    fun getSinglePost(postNum: Int) {
        getSinglePost(this.board.id, postNum)
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

    @SuppressLint("CheckResult")
    fun hideThread(threadNum: Int, toHide: Boolean) {
        if (toHide) {
            threadInteractor.markThreadHidden(board.id, board.name, threadNum)
                .subscribe(
                    {},
                    { Log.e("M_BoardPresenter","hidding error = $it") }
                )
        }
        else {
            threadInteractor.unmarkThreadHidden(board.id, threadNum)
                .subscribe()
        }
    }

    fun setBoardMarks(){
        viewState?.setBoardMarks(board.isFavorite)
    }

    fun getBoardId() =
        board.id

}
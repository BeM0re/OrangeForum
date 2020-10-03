package ru.be_more.orange_forum.ui.board

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.ui.PresentationContract
import java.util.*

class BoardViewModelImpl (
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor
): PresentationContract.BoardViewModel {

    override val board = MutableLiveData<Board>()
    override val isFavorite = MutableLiveData<Boolean>()
    override val post = MutableLiveData<Post>()
    override val attachment = MutableLiveData<Attachment>()
    override val emptyStack = MutableLiveData<Boolean>()
    override val savedPosition = MutableLiveData<Int>()

    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    override fun init(boardId: String?, boardName: String?){
        if (board.value == null || board.value?.id != boardId){
            if (!boardId.isNullOrEmpty() && !boardName.isNullOrEmpty()) {
                boardInteractor.getBoard(boardId, boardName)
                    .subscribe(
                        { board ->
                            this.board.postValue(board)
                            isFavorite.postValue(board.isFavorite)
                        },
                        { Log.e("M_BoardPresenter", "Getting board error = $it") }
                    )
            }
        }
        else{
            board.postValue(board.value)
            isFavorite.postValue(isFavorite.value)
            savedPosition.postValue(savedPosition.value)
        }
    }

    override fun onDestroy() {
        boardInteractor.release()
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
        getSinglePost(board.value?.id?:"", postNum)
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

    @SuppressLint("CheckResult")
    override fun hideThread(threadNum: Int, toHide: Boolean) {
        if (toHide) {
            threadInteractor
                .markThreadHidden(board.value?.id?:"", board.value?.name?:"", threadNum)
                .subscribe(
                    {},
                    { Log.e("M_BoardPresenter","hidding error = $it") }
                )
        }
        else {
            threadInteractor.unmarkThreadHidden(board.value?.id?:"", threadNum)
                .subscribe()
        }
    }

    override fun setBoardMarks(){
        isFavorite.postValue(board.value?.isFavorite)
    }

    override fun getBoardId() =
        board.value?.id

    override fun savePosition(pos: Int){
        savedPosition.postValue(pos)
    }

    override fun setFavorite(isFavorite: Boolean) {
        if (board.value != null)
            if (isFavorite)
                boardInteractor
                    .markBoardFavorite(board.value!!.id, board.value!!.name)
                    .subscribe()
            else
                boardInteractor
                    .unmarkBoardFavorite(board.value!!.id)
                    .subscribe()
    }

    override fun getBoardName(): String =
        board.value?.name?:""
}
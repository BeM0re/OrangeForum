package ru.be_more.orange_forum.ui.board

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import java.util.*

class BoardViewModel (
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor
): ViewModel(){

    private var firstLaunch = true
    var board = MutableLiveData<Board>()
    var isFavorite = MutableLiveData<Boolean>()
    var post = MutableLiveData<Post>()
    var attachment = MutableLiveData<Attachment>()
    var emptyStack = MutableLiveData<Boolean>()
    var savedPosition = MutableLiveData<Int>()

    private val modalStack: Stack<ModalContent> = Stack()

    @SuppressLint("CheckResult")
    fun init(boardId: String?){
        if (firstLaunch){
            if (!boardId.isNullOrEmpty()) {
                boardInteractor.getBoard(boardId)
                    .subscribe(
                        { board ->
                            this.board.postValue(board)
                            isFavorite.postValue(board.isFavorite)
                            firstLaunch = false
                        },
                        {
                            Log.e("M_BoardPresenter", "Getting board error = $it")
                        }
                    )
            }
        }
        else{
            board.postValue(board.value)
            isFavorite.postValue(isFavorite.value)
            savedPosition.postValue(savedPosition.value)
        }
    }

    fun onDestroy() {
        boardInteractor.release()
        threadInteractor.release()
        postInteractor.release()
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
                is Attachment -> attachment.postValue(content)
                is Post -> post.postValue(content)
            }
        } else
            emptyStack.postValue(true)
    }

    fun getSinglePost(postNum: Int) {
        getSinglePost(board.value?.id?:"", postNum)
    }

    @SuppressLint("CheckResult")
    fun getSinglePost(boardId: String, postNum: Int){
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
    fun hideThread(threadNum: Int, toHide: Boolean) {
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

    fun setBoardMarks(){
        isFavorite.postValue(board.value?.isFavorite)
    }

    fun getBoardId() =
        board.value?.id

    fun savePosition(pos: Int){
        savedPosition.postValue(pos)
    }
}
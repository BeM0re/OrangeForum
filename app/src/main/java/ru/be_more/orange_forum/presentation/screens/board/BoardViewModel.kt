package ru.be_more.orange_forum.presentation.screens.board

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import org.greenrobot.eventbus.EventBus
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.bus.ThreadToBeClosed
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import java.util.*

class BoardViewModel(
    private val boardId: String,
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences,
): BaseViewModel() {

    val board = MutableLiveData<Board>()
    val isFavorite = MutableLiveData<Boolean>()
    val post = MutableLiveData<Post>()
    val attachment = MutableLiveData<Attachment>()
    val emptyStack = MutableLiveData<Boolean>()
    val savedPosition = MutableLiveData<Int>()

    private val modalStack: Stack<ModalContent> = Stack()

    var text by mutableStateOf("asd")
        private set

    init {
        text = boardId
    }

    fun init(boardId: String?, boardName: String?){

//        EventBus.getDefault().post(BoardToBeOpened)
        if (board.value == null || (board.value?.id != boardId && !boardId.isNullOrEmpty())){
            EventBus.getDefault().post(ThreadToBeClosed)
            if (!boardId.isNullOrEmpty() && !boardName.isNullOrEmpty())
                boardInteractor.get(boardId)
                    .defaultThreads()
                    .subscribe(
                        { board ->
                            this.board.postValue(board)
                            isFavorite.postValue(board.isFavorite)
                        },
                        { Log.e("M_BoardViewModel", "Getting board error = $it") }
                    )
                    .addToSubscribe()
        }
        else{
            board.postValue(board.value)
            isFavorite.postValue(isFavorite.value)
            savedPosition.postValue(savedPosition.value)
        }
    }

    fun clearStack() {
        this.modalStack.clear()
        emptyStack.postValue(true)
    }

    fun putContentInStack(modal: ModalContent) {
        this.modalStack.push(modal)
    }

    fun onBackPressed() {
        modalStack.pop()
        if (!modalStack.empty()) {
            when (val content = modalStack.peek()) {
                is Attachment -> attachment.postValue(content)
                is Post -> post.postValue(content)
            }
        } else
            emptyStack.postValue(true)
    }

    fun getSinglePost(postNum: Int) {
        getSinglePost(board.value?.id?:"", postNum)
    }

    fun getSinglePost(boardId: String, postNum: Int) {
        postInteractor.getPost(boardId, postNum)
            .defaultThreads()
            .subscribe(
                {
//                        this.putContentInStack(it) //todo
//                        post.postValue(it)
                },
                { error.postValue("Пост не найден") }
            )
            .addToSubscribe()
    }

    fun hideThread(threadNum: Int, toHide: Boolean) {
        threadInteractor
            .markHidden(
                boardId = requireNotNull(board.value?.id),
                boardName = requireNotNull(board.value?.name),
                threadNum = threadNum,
            )
            .andThen(
                boardInteractor.get(
                    requireNotNull(board.value?.id),
                )
            )
            .defaultThreads()
            .subscribe(
                { board.postValue(it) },
                { Log.e("M_BoardViewModel","hiding error = $it") }
            )
            .addToSubscribe()
    }

    fun setBoardMarks(){
        isFavorite.postValue(board.value?.isFavorite)
    }

     fun getBoardId() =
        board.value?.id

     fun savePosition(pos: Int){
        savedPosition.postValue(pos)
    }

     fun setFavorite(isFavorite: Boolean) {
        if (board.value != null)
            boardInteractor
                .markFavorite(board.value!!.id)
                .defaultThreads()
                .subscribe(
                    {
                        this.isFavorite.postValue(true)
                        prefs.favsToUpdate = true
                    },
                    { Log.e("M_BoardViewModelImpl","Adding fav error = $it") }
                )
                .addToSubscribe()
    }

     fun getBoardName(): String =
        board.value?.name?:""

     fun addToQueue(threadNum: Int) {
        if (board.value != null)
            threadInteractor
                .markQueued(
                    boardId = requireNotNull(board.value?.id),
                    boardName = requireNotNull(board.value?.name),
                    threadNum = threadNum,
                )
                .defaultThreads()
                .subscribe(
                    { prefs.queueToUpdate = true },
                    { Log.e("M_BoardViewModelImpl","Add to queue error = $it") }
                )
                .addToSubscribe()
    }

     fun onMenuReady() {
        isFavorite.postValue(isFavorite.value)
    }

     fun prepareModal(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {
        if (!fullPicUrl.isNullOrEmpty() || fullPicUri != null)
            Attachment(fullPicUrl, duration, fullPicUri).also {
                putContentInStack(it)
                attachment.postValue(it)
            }
    }

     fun linkClicked(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            error.postValue("Пост не найден")
        else
            getSinglePost(chanLink.first, chanLink.third)
    }

    companion object {
        val boardIdKey = object : CreationExtras.Key<String>{}
    }
}
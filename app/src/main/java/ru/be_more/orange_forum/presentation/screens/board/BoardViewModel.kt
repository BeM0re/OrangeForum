package ru.be_more.orange_forum.presentation.screens.board

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.presentation.composeViews.ModalContentDialogInitArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import java.util.*

class BoardViewModel(
    private val boardId: String,
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences,
): BaseViewModel() {

    private val modalStack: Stack<ModalContent> = Stack()

    var items by mutableStateOf(listOf<OpPostInitArgs>())
        private set

    var screenTitle by mutableStateOf("")
        private set

    var isFavorite by mutableStateOf(false)
        private set

    var modalContent by mutableStateOf<ModalContentDialogInitArgs?>(null)
        private set

    init {
        boardInteractor
            .get(boardId)
            .defaultThreads()
            .subscribe(
                { board ->
                    screenTitle = board.name
                    isFavorite = board.isFavorite
                    items = prepareItemList(
                        board.threads
                    )
                },
                { Log.e("BoardViewModel", "BoardViewModel.init: \n $it") }
            )
            .addToSubscribe()
    }

    private fun prepareItemList(threads: List<BoardThread>): List<OpPostInitArgs> =
        threads.mapNotNull { thread ->
            val post = thread.posts.getOrNull(0) ?: return@mapNotNull null

            OpPostInitArgs(
                post = post,
                onHide = ::hideThread,
                onQueue = ::addToQueue,
                onPick = ::onPick
            )
        }

    private fun addToQueue(boardId: String, threadNum: Int) {
        threadInteractor
            .markQueued(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { prefs.queueToUpdate = true },
                { Log.e("BoardViewModel","BoardViewModel.addToQueue: \n $it") }
            )
            .addToSubscribe()
    }

    private fun hideThread(boardId: String, threadNum: Int) {
        threadInteractor
            .markHidden(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("BoardViewModel","BoardViewModel.hideThread: \n $it") }
            )
            .addToSubscribe()
    }

    private fun onPick(file: AttachedFile) {
        modalContent = ModalContentDialogInitArgs(
            content = file,
            onPicClick = ::onPick,
            onDismiss = ::closeModal
        )
    }

    fun setFavorite() {
        boardInteractor
            .markFavorite(boardId)
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("BoardViewModel","BoardViewModel.setFavorite: \n $it") }
            )
            .addToSubscribe()
    }

    fun closeModal() {
        //todo add stack into super
        modalContent = null
    }

/*    fun init(boardId: String?, boardName: String?){

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

    fun setBoardMarks(){
        isFavorite.postValue(board.value?.isFavorite)
    }

     fun getBoardId() =
        board.value?.id

     fun savePosition(pos: Int){
        savedPosition.postValue(pos)
    }



     fun getBoardName(): String =
        board.value?.name?:""

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
    */
}
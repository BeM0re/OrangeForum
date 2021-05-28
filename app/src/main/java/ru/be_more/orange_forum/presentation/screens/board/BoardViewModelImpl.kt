package ru.be_more.orange_forum.presentation.screens.board

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.bus.BoardToBeOpened
import ru.be_more.orange_forum.presentation.bus.ThreadToBeClosed
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModelImpl
import java.util.*

class BoardViewModelImpl (
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences
): PresentationContract.BoardViewModel, BaseViewModelImpl() {

    override val board = MutableLiveData<Board>()
    override val isFavorite = MutableLiveData<Boolean>()
    override val post = MutableLiveData<Post>()
    override val attachment = MutableLiveData<Attachment>()
    override val emptyStack = MutableLiveData<Boolean>()
    override val savedPosition = MutableLiveData<Int>()

    private val modalStack: Stack<ModalContent> = Stack()

    override fun init(boardId: String?, boardName: String?){
        EventBus.getDefault().post(BoardToBeOpened)
        if (board.value == null || (board.value?.id != boardId && !boardId.isNullOrEmpty())){
            EventBus.getDefault().post(ThreadToBeClosed)
            if (!boardId.isNullOrEmpty() && !boardName.isNullOrEmpty()) {
                disposables.add(
                    boardInteractor.getBoard(boardId, boardName)
                        .subscribe(
                            { board ->
                                this.board.postValue(board)
                                isFavorite.postValue(board.isFavorite)
                            },
                            { Log.e("M_BoardViewModel", "Getting board error = $it") }
                        )
                )
            }
        }
        else{
            board.postValue(board.value)
            isFavorite.postValue(isFavorite.value)
            savedPosition.postValue(savedPosition.value)
        }
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

    override fun getSinglePost(boardId: String, postNum: Int){
        disposables.add(
            postInteractor.getPost(boardId, postNum)
                .subscribe(
                    {
                        this.putContentInStack(it)
                        post.postValue(it)
                    },
                    { error.postValue("Пост не найден") }
                )
        )
    }

    override fun hideThread(threadNum: Int, toHide: Boolean) {
        if (toHide) {
            disposables.add(
                threadInteractor
                    .hideThread(board.value?.id ?: "", board.value?.name ?: "", threadNum)
                    .andThen(boardInteractor.getBoard(board.value?.id ?: "", board.value?.name ?: ""))
                    .subscribe(
                        { board.postValue(it) },
                        { Log.e("M_BoardViewModel","hiding error = $it") }
                    )
            )
        }
        else {
            disposables.add(
                threadInteractor.unhideThread(board.value?.id ?: "", threadNum)
                    .andThen(boardInteractor.getBoard(board.value?.id ?: "", board.value?.name ?: ""))
                    .subscribe(
                        { board.postValue(it) },
                        { Log.e("M_BoardViewModel","hiding error = $it") }
                    )
            )
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
                disposables.add(
                    boardInteractor
                        .markBoardFavorite(board.value!!.id, board.value!!.name)
                        .subscribe(
                            { this.isFavorite.postValue(true)
                                prefs.favsToUpdate = true},
                            { Log.e("M_BoardViewModelImpl","Adding fav error = $it") }
                        )
                )
            else
                disposables.add(
                    boardInteractor
                        .unmarkBoardFavorite(board.value!!.id)
                        .subscribe(
                            { this.isFavorite.postValue(false)
                                prefs.favsToUpdate = true},
                            { Log.e("M_BoardViewModelImpl","Removing fav error = $it") }
                        )
                )
    }

    override fun getBoardName(): String =
        board.value?.name?:""

    override fun addToQueue(threadNum: Int) {
        if (board.value != null)
            disposables.add(
                threadInteractor
                    .addThreadToQueue(threadNum, board.value?.id ?: return, board.value?.name ?: return)
                    .subscribe(
                        { prefs.queueToUpdate = true },
                        { Log.e("M_BoardViewModelImpl","Add to queue error = $it") }
                    )
            )
    }

    override fun onMenuReady() {
        isFavorite.postValue(isFavorite.value)
    }

    override fun prepareModal(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {
        if (!fullPicUrl.isNullOrEmpty() || fullPicUri != null)
            Attachment(fullPicUrl, duration, fullPicUri).also {
                putContentInStack(it)
                attachment.postValue(it)
            }
    }

    override fun linkClicked(chanLink: Triple<String, Int, Int>?) {
        if (chanLink?.first.isNullOrEmpty() || chanLink?.third == null)
            error.postValue("Пост не найден")
        else
            getSinglePost(chanLink.first, chanLink.third)
    }
}
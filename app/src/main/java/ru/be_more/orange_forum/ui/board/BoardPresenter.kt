package ru.be_more.orange_forum.ui.board

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.model.*
import ru.be_more.orange_forum.repositories.DvachApiRepository
import java.util.*
import javax.inject.Inject

@InjectViewState
class BoardPresenter : MvpPresenter<BoardView>() {

    @Inject
    lateinit var repo : DvachApiRepository
    private lateinit var board :Board
    private var disposables: LinkedList<Disposable?> = LinkedList()
    private var boardId: String = ""
    var listener: ((threadNum: Int, threadTitle: String) -> Unit)? = null

    private val modalStack: Stack<ModalContent> = Stack()

    fun init(boardId: String, listener: ((threadNum: Int, threadTitle: String) -> Unit)?){

        App.getComponent().inject(this)

        if (listener!=null)
            this.listener = listener

        if (!boardId.isNullOrEmpty())
            this.boardId = boardId

        disposables.add(repo.getBoard(this.boardId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                board = Board("", this.boardId, it)
                viewState.loadBoard(board)
            }
        )
    }

    override fun onDestroy() {
        disposables.forEach { it?.dispose() }
        super.onDestroy()
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

    fun getSinglePost(boardId: String, postNum: Int){
        disposables.add(
            repo.getPost(boardId, postNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        this.putContentInStack(it)
                        viewState.showPost(it)
                    },
                    { viewState.showToast("Пост не найден") }
                )

        )
    }


}
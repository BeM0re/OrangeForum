package ru.be_more.orange_forum.ui.board

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.interactors.ThreadInteractor
import ru.be_more.orange_forum.domain.model.*
import java.util.*
import javax.inject.Inject

@InjectViewState
class BoardPresenter : MvpPresenter<BoardView>() {


    @Inject
    lateinit var interactor : ThreadInteractor

    @Inject
    lateinit var repo : DvachApiRepository
    private var board :Board = Board("", "", listOf(), false)
    private var disposables: LinkedList<Disposable?> = LinkedList()
    private var disposable: Disposable? = null
    private var boardId: String = ""
    var listener: ((threadNum: Int, threadTitle: String) -> Unit)? = null

    private val modalStack: Stack<ModalContent> = Stack()

    fun init(boardId: String, listener: ((threadNum: Int, threadTitle: String) -> Unit)?){

        App.getComponent().inject(this)

        if (listener!=null)
            this.listener = listener

        if (!boardId.isNullOrEmpty())
            this.boardId = boardId

//        disposables.add(
        disposable =
            interactor.getBoard(this.boardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ board ->
                    viewState.loadBoard(board)
                    viewState.setBoardMarks(board.isFavorite)
                },
                    {
                        Log.e("M_BoardPresenter","Getting board error = $it")
                })
//        )
    }

    override fun onDestroy() {
//        disposables.forEach { it?.dispose() }
        disposable?.dispose()
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
        disposable?.dispose()
        disposable =
//        disposables.add(
            repo.getPost(boardId, postNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        this.putContentInStack(it)
                        viewState.showPost(it)
                    },
                    { App.showToast("Пост не найден") }
                )

//        )
    }

    fun hideThread(threadNum: Int, isHidden: Boolean) {
        if (!isHidden)
            interactor.markThreadHidden(boardId, threadNum)
        else {
//            disposables.add(
            disposable?.dispose()
            disposable =
                interactor.unmarkThreadHidden(boardId, threadNum)
                    .subscribeOn(Schedulers.io())
                    .subscribe({},
                        { Log.d("M_BoardPresenter", "error = $it") })
//            )
        }
    }

    fun setBoardMarks(){
        viewState.setBoardMarks(board.isFavorite)
    }

  /*  fun favoriteThread(threadNum: Int, isFavorite: Boolean) {
        if (isFavorite)
            interactor.markThreadFavorite(boardId, threadNum, board.title)
        else
            interactor.unmarkThreadFavorite(boardId, threadNum)
    }
*/
}
package ru.be_more.orange_forum.ui.favorire

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.interactors.ThreadInteractor
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.ModalContent
import ru.be_more.orange_forum.model.Post
import java.util.*
import javax.inject.Inject

@InjectViewState
class FavoritePresenter : MvpPresenter<FavoriteView>() {

    @Inject
    lateinit var interactor : ThreadInteractor
    private var disposables : LinkedList<Disposable?> = LinkedList()

    private val modalStack: Stack<ModalContent> = Stack()
    private lateinit var boards : List<Board>
    private var timestamp: Long = 0

    init {
        App.getComponent().inject(this)
    }

    override fun onFirstViewAttach(){
        disposables.add(
            interactor.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ boards ->
                    this.boards = boards
                    viewState.loadFavorites()
                },
                    { Log.d("M_DownloadPresenter", "Presenter on first view attach error = $it") }
                )
        )
    }

    fun refreshData(){ //TODO не работает
        Log.d("M_FavoritePresenter","refresh")
        disposables.add(
            interactor.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ boards ->
                    this.boards = boards
                    viewState.loadFavorites()
                },
                    { Log.d("M_DownloadPresenter", "Presenter on refresh error = $it") }
                )
        )
    }

    override fun onDestroy() {
        disposables.forEach { it?.dispose() }
        super.onDestroy()
    }

    fun putContentInStack(content: ModalContent) {
        this.modalStack.push(content)
    }


    fun getSinglePost(boardId: String, postNum: Int){
        disposables.add(
            interactor.getPost(boardId, postNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        this.putContentInStack(it)
                        viewState.showPost(it)
                    },
                    {  App.showToast("Пост не найден" ) }
                )

        )
    }

    fun clearStack() {
        this.modalStack.clear()
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

    fun getBoards(): List<Board> = this.boards
}
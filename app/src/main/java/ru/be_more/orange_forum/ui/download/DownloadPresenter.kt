package ru.be_more.orange_forum.ui.download

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
//import moxy.InjectViewState
//import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.ModalContent
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.ui.main.MainView
import java.util.*
//import javax.inject.Inject

//@InjectViewState
class DownloadPresenter /*@Inject constructor*/(
    private val downloadInteractor : InteractorContract.DownloadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val viewState: DownloadView
)/* : MvpPresenter<DownloadView>()*/ {

    private val modalStack: Stack<ModalContent> = Stack()
    private lateinit var boards : List<Board>

    @SuppressLint("CheckResult")
//    override fun onFirstViewAttach(){
    fun initPresenter(){
        downloadInteractor.getDownloads()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ boards ->
                this.boards = boards
                viewState.loadDownloads()
            },
                { Log.d("M_DownloadPresenter", "Presenter on first view attach error = $it") }
            )
    }

    /*override*/ fun onDestroy() {
        downloadInteractor.release()
        postInteractor.release()
//        super.onDestroy()
    }

    fun putContentInStack(content: ModalContent) {
        this.modalStack.push(content)
    }

    @SuppressLint("CheckResult")
    fun getSinglePost(boardId: String, postNum: Int){
        postInteractor.getPost(boardId, postNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    this.putContentInStack(it)
                    viewState.showPost(it)
                },
                {  App.showToast("Пост не найден" ) }
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
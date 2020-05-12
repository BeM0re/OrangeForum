package ru.be_more.orange_forum.ui.download

import android.os.Handler
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.ModalContent
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.repositories.DvachDbRepository
import java.util.*
import javax.inject.Inject

@InjectViewState
class DownloadPresenter : MvpPresenter<DownloadView>() {

    @Inject
    lateinit var apiRepo : DvachApiRepository
    @Inject
    lateinit var dbRepo : DvachDbRepository
    private var disposables : LinkedList<Disposable?> = LinkedList()

    private val modalStack: Stack<ModalContent> = Stack()
    private lateinit var boards : List<Board>
    private var timestamp: Long = 0

    init {
        App.getComponent().inject(this)
    }

    override fun onFirstViewAttach(){
        /*disposables.add(
            dbRepo.getBoardNames()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{boards ->
                    boards.map { board ->
                        dbRepo.getThreadOpPosts(board.id)
                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe (
                                { threads -> board.threads = threads },
                                { Log.d("M_DownloadPresenter", "error = $it") }
                            )
                    }
                    viewState.loadDownloads(boards)
                }
        )*/
        disposables.add(
            dbRepo.getBoards()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ boards ->
                    this.boards = boards
                    Handler().postDelayed({
                        viewState.loadDownloads()
                    }, 10)
                },
                    {Log.d("M_DownloadPresenter", "Presenter on first view attach error = $it")}
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
            dbRepo.getPost(boardId, postNum)
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
package ru.be_more.orange_forum.ui.download

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.model.Attachment
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

    init {
        App.getComponent().inject(this)
    }

    override fun onFirstViewAttach(){
        disposables.add(
            dbRepo.getDownloads()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{viewState.loadDownloads(it)}
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
}
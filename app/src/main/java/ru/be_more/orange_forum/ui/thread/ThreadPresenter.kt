package ru.be_more.orange_forum.ui.thread

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private var repo = DvachApiRepository
    var thread :BoardThread = BoardThread(num = 0)
    private lateinit var boardId :String
    private var threadNum :Int = 0
    private var disposable : Disposable? = null

    fun init(boardId: String, threadNum: Int){
        this.boardId = boardId
        this.threadNum = threadNum
        disposable?.dispose()
        disposable = repo.getThread(boardId, threadNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                thread = it
                viewState.loadThread(thread)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

}
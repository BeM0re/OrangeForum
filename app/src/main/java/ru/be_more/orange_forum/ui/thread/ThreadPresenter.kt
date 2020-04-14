package ru.be_more.orange_forum.ui.thread

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private var isResponseOpen : MutableLiveData<Boolean> = MutableLiveData()

    private var repo = DvachApiRepository
    var thread :BoardThread = BoardThread(num = 0)
    private lateinit var boardId :String
    private var threadNum :Int = 0
    private var repoDisposable : Disposable? = null

    fun openResponseForm(){
        isResponseOpen.postValue(true)
    }

    fun getIsResponseOpen(): LiveData<Boolean> = isResponseOpen

    fun updateThreadData(){
        viewState.loadThread(thread)
    }

    fun init(boardId: String, threadNum: Int){
        this.boardId = boardId
        this.threadNum = threadNum
        repoDisposable?.dispose()
        repoDisposable = repo.getThread(boardId, threadNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                thread = it
                viewState.loadThread(thread)
            }

        getIsResponseOpen().observeForever { viewState.hideResponseFab() }

    }

    override fun onDestroy() {
        super.onDestroy()
        repoDisposable?.dispose()
    }

}
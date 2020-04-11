package ru.be_more.orange_forum.ui.thread

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private var repo = DvachApiRepository
    var thread :BoardThread = BoardThread(num = 0)
    private lateinit var boardId :String
    private var threadNum :Int = 0
    private var disposable : Disposable? = null

    private fun getParseData() : LiveData<BoardThread> = runBlocking {
        return@runBlocking repo.getThread(boardId, threadNum)
    }

    private fun getParseDataRx() : Observable<BoardThread> = runBlocking {
        return@runBlocking repo.getThreadRx(boardId, threadNum)
    }

    fun updateThreadData(){
        viewState.loadThread(thread)
    }

    fun init(boardId: String, threadNum: Int){
        this.boardId = boardId
        this.threadNum = threadNum
        getParseData().observeForever( Observer {
            thread=it
            viewState.loadThread(thread)
            Log.d("M_ThreadPresenter", "thread = ${thread.posts[0].files}")
        })
        updateThreadData()
    }

    fun initRx(boardId: String, threadNum: Int){
        this.boardId = boardId
        this.threadNum = threadNum
        disposable = getParseDataRx()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                thread = it
                updateThreadData()
            }

    }

}
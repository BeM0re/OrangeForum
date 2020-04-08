package ru.be_more.orange_forum.ui.thread

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private var repo = DvachApiRepository
    var thread :MutableLiveData<BoardThread> = MutableLiveData()
    private lateinit var board :Board

    private fun getParseData() : MutableLiveData<BoardThread> = runBlocking {

        Log.d("M_ThreadPresenter", "get = ${thread.value}")
        if(thread.value!=null) {
            return@runBlocking repo.getThread(board, thread.value!!.num) as MutableLiveData
        }
        else
            return@runBlocking thread
    }

    fun updateThreadData(){
        thread = getParseData()
    }

    fun setThread(boardId: String, threadId: Int){
        board = Board("", boardId)
        thread.postValue(BoardThread(threadId))

        thread = getParseData()

        if(thread.value != null) {
            viewState.loadThread(thread.value!!)
            Log.d("M_ThreadPresenter", "load = ${thread.value}")
        }
    }

}
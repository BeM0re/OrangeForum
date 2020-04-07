package ru.be_more.orange_forum.ui.thread

import android.util.Log
import androidx.lifecycle.LiveData
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
    private lateinit var thread :BoardThread
    private lateinit var board :Board

    private fun getParseData() : LiveData<BoardThread> = runBlocking {
        return@runBlocking  repo.getThread(board, thread.num)
    }

    fun setThread(boardId: String, threadId: Int){
        board = Board("", boardId)
        thread = BoardThread(threadId)

        getParseData().observeForever( Observer {
            thread=it
            viewState.loadThread(thread)
            Log.d("M_ThreadPresenter", "thread = ${thread.posts[0].files}")
        })
    }

}
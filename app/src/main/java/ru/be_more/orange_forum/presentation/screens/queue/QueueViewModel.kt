package ru.be_more.orange_forum.presentation.screens.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class QueueViewModel (
    private val queueInteractor : InteractorContract.QueueInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val prefs: Preferences
): BaseViewModel(){

     val boards = MutableLiveData<List<Board>>()
    private var queueDisposable: Disposable? = null

     fun init(){
        subscribe()
    }

     override fun onDestroy() {
        super.onDestroy()

        queueDisposable?.dispose()
        queueDisposable = null
    }

     fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor
            .markQueued(
                boardId = boardId,
                threadNum = threadNum,
            )
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("M_QueueViewModelImpl","removing from queue error = $it") }
            )
            .addToSubscribe()
    }

    private fun subscribe(){
        queueDisposable?.dispose()
        queueDisposable = queueInteractor
            .observe()
            .defaultThreads()
            .subscribe(
                { boards ->
                    this.boards.postValue(boards)
                },
                { Log.e("M_QueueViewModelImpl", "Presenter on refresh error = $it") }
            )

    }
}
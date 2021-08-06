package ru.be_more.orange_forum.presentation.screens.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModelImpl

class QueueViewModelImpl (
    private val queueInteractor : InteractorContract.QueueInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val prefs: Preferences
): PresentationContract.QueueViewModel, BaseViewModelImpl(){

    override val boards = MutableLiveData<List<Board>>()
    private var queueDisposable: Disposable? = null

    override fun init(){
        subscribe()
    }

    override fun onDestroy() {
        queueDisposable?.dispose()
        queueDisposable = null
        super.onDestroy()
    }

    override fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor
            .markThreadQueued(
                boardId = boardId,
                boardName = "",
                threadNum = threadNum,
                isQueued = false
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
            .getQueueObservable()
            .defaultThreads()
            .subscribe(
                { boards ->
                    this.boards.postValue(boards)
                },
                { Log.e("M_QueueViewModelImpl", "Presenter on refresh error = $it") }
            )

    }
}
package ru.be_more.orange_forum.presentation.screens.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract

class QueueViewModelImpl (
    private val queueInteractor : InteractorContract.QueueInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences
): PresentationContract.QueueViewModel{

    override val boards = MutableLiveData<List<Board>>()
    private var disposables: CompositeDisposable? = CompositeDisposable()

    override fun init(){
        if(boards.value == null || prefs.queueToUpdate)
            refreshData()
        else
            boards.postValue(boards.value)
    }

    private fun refreshData(){
        disposables?.add(
            queueInteractor.getQueue()
                .subscribe(
                    { boards -> this.boards.postValue(boards) },
                    { Log.e("M_QueueViewModelImpl", "Presenter on refresh error = $it") }
                )
        )
    }

    override fun removeThread(boardId: String, threadNum: Int) {
        disposables?.add(
            threadInteractor.removeThreadFromQueue(boardId, threadNum)
                .subscribe(
                    { refreshData() },
                    { Log.e("M_QueueViewModelImpl","removing from queue error = $it")}
                )
        )
    }

    override fun onDestroy() {
        disposables?.dispose()
        disposables = null
    }
}
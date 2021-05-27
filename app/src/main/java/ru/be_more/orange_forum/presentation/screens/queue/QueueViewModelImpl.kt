package ru.be_more.orange_forum.presentation.screens.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModelImpl

class QueueViewModelImpl (
    private val queueInteractor : InteractorContract.QueueInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences
): PresentationContract.QueueViewModel, BaseViewModelImpl(){

    override val boards = MutableLiveData<List<Board>>()

    override fun init(){
        if(boards.value == null || prefs.queueToUpdate) {
            refreshData()
            prefs.queueToUpdate = false
        }
        else
            boards.postValue(boards.value)
    }

    private fun refreshData(){
        disposables.add(
            loadData()
        )
    }

    override fun removeThread(boardId: String, threadNum: Int) {
        disposables.add(
            threadInteractor.removeThreadFromQueue(boardId, threadNum)
                .andThen { loadData() }
                .subscribe(
                    { },
                    { Log.e("M_QueueViewModelImpl","removing from queue error = $it")}
                )
        )
    }

    private fun loadData() =
        queueInteractor.getQueue()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_QueueViewModelImpl", "Presenter on refresh error = $it") }
            )

}
package ru.be_more.orange_forum.presentation.screens.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract

class QueueViewModelImpl (
    private val queueInteractor : InteractorContract.QueueInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val prefs: Preferences
): PresentationContract.QueueViewModel{

    override val boards = MutableLiveData<List<Board>>()

    override fun init(){
        if(boards.value == null || prefs.queueToUpdate)
            refreshData()
        else
            boards.postValue(boards.value)
    }

    override fun refreshData(){
        queueInteractor.getQueue()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownloadPresenter", "Presenter on refresh error = $it") }
            )
    }

    override fun onDestroy() {
        queueInteractor.release()
        postInteractor.release()
    }

}
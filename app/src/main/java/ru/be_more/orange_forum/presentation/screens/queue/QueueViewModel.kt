package ru.be_more.orange_forum.presentation.screens.queue

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract

class FavoriteViewModelImpl (
    private val QueueInteractor : InteractorContract.QueueInteractor,
    private val postInteractor : InteractorContract.PostInteractor
): PresentationContract.FavoriteViewModel{

    override val boards = MutableLiveData<List<Board>>()

    @SuppressLint("CheckResult")
    override fun init(){
        if(boards.value == null)
            refreshData()
        else
            boards.postValue(boards.value)
    }

    @SuppressLint("CheckResult")
    override fun refreshData(){
        QueueInteractor.getQueue()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownloadPresenter", "Presenter on refresh error = $it") }
            )
    }

    override fun onDestroy() {
        QueueInteractor.release()
        postInteractor.release()
    }

}
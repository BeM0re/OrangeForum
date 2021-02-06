package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract

class DownFavViewModelImpl (
    private val downFavInteractor : InteractorContract.DownFavInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val prefs: Preferences
): PresentationContract.DownFavViewModel {

    override val boards = MutableLiveData<List<Board>>()

    override fun init(){
        if(boards.value == null || prefs.favsToUpdate)
            refreshData()
        else
            boards.postValue(boards.value)
    }

    private fun refreshData(){
        downFavInteractor.getDownloads()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownloadPresenter", "Presenter on first view attach error = $it") }
            )
    }

    override fun onDestroy() {
        downFavInteractor.release()
        postInteractor.release()
    }

    override fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor.deleteThread(boardId, threadNum)
            .subscribe()
    }
}
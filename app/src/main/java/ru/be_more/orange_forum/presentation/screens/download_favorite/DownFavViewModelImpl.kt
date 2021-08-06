package ru.be_more.orange_forum.presentation.screens.download_favorite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModelImpl

class DownFavViewModelImpl (
    private val downFavInteractor : InteractorContract.DownFavInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val prefs: Preferences
): PresentationContract.DownFavViewModel, BaseViewModelImpl() {

    override val boards = MutableLiveData<List<Board>>()

    override fun init(){
        if(boards.value == null || prefs.favsToUpdate) {
            refreshData()
            prefs.favsToUpdate = false
        }
        else
            boards.postValue(boards.value)
    }

    private fun refreshData(){
        downFavInteractor.getDownFavs()
            .defaultThreads()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownFavViewModelImpl", "Presenter on first view attach error = $it") }
            )
            .addToSubscribe()
    }

    override fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor
            .deleteThread(boardId, threadNum)
            .andThen (downFavInteractor.getDownFavs())
            .defaultThreads()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownFavViewModelImpl","removing from queue error = $it")}
            )
            .addToSubscribe()

        threadInteractor
            .markThreadFavorite(
                boardId = boardId,
                boardName = "",
                threadNum = threadNum,
                isFavorite = false
            )
            .andThen (downFavInteractor.getDownFavs())
            .defaultThreads()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownFavViewModelImpl","removing from queue error = $it")}
            )
            .addToSubscribe()

    }
}
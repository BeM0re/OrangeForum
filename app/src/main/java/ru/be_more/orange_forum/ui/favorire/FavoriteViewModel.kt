package ru.be_more.orange_forum.ui.favorire

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.ui.PresentationContract

class FavoriteViewModelImpl (
    private val favoriteInteractor : InteractorContract.FavoriteInteractor,
    private val postInteractor : InteractorContract.PostInteractor
): PresentationContract.FavoriteViewModel{

    override val boards = MutableLiveData<List<Board>>()

    @SuppressLint("CheckResult")
    override fun init(){
        if(boards.value == null)
            favoriteInteractor.getFavorites()
                .subscribe(
                    { boards -> this.boards.postValue(boards) },
                    { Log.e("M_DownloadPresenter", "Presenter on first view attach error = $it") }
                )
        else
            boards.postValue(boards.value)
    }

    @SuppressLint("CheckResult")
    override fun refreshData(){
        favoriteInteractor.getFavorites()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownloadPresenter", "Presenter on refresh error = $it") }
            )
    }

    override fun onDestroy() {
        favoriteInteractor.release()
        postInteractor.release()
    }

}
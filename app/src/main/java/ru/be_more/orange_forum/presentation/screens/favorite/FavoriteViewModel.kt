package ru.be_more.orange_forum.presentation.screens.favorite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class FavoriteViewModel (
    private val favoriteInteractor : InteractorContract.FavoriteInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val prefs: Preferences
): BaseViewModel() {

     val boards = MutableLiveData<List<Board>>()
    private var favDisposable: Disposable? = null

     fun init(){
        subscribe()
        checkUpdates()
    }

     override fun onDestroy() {
        super.onDestroy()

        favDisposable?.dispose()
        favDisposable = null
    }

     fun removeThread(boardId: String, threadNum: Int) {
        threadInteractor
            .delete(boardId, threadNum)
            .defaultThreads()
            .subscribe(
                {  },
                { Log.e("M_DownFavViewModelImpl","removing from queue error = $it")}
            )
            .addToSubscribe()

        threadInteractor
            .markFavorite(
                boardId = boardId,
                threadNum = threadNum,
            )
            .defaultThreads()
            .subscribe(
                {  },
                { Log.e("M_DownFavViewModelImpl","removing from queue error = $it")}
            )
            .addToSubscribe()
    }

    private fun subscribe(){
        favDisposable?.dispose()
        favDisposable = favoriteInteractor
            .observe()
            .defaultThreads()
            .subscribe(
                { boards -> this.boards.postValue(boards) },
                { Log.e("M_DownFavViewModelImpl", "Presenter on first view attach error = $it") }
            )
    }

    private fun checkUpdates(){
        favoriteInteractor
            .observe()
            .flatMap { boards ->
                Observable.fromIterable(
                    boards.map { board ->
                        board.threads.map { thread ->
                            board.id to thread.num
                        }
                    }.flatten()
                )
            }
            .flatMapCompletable { (boardId, threadNum) ->
                threadInteractor.updateNewMessages(boardId, threadNum)
            }
            .defaultThreads()
            .subscribe(
                { },
                { Log.e("M_DownFavViewModelImpl", "Presenter on first view attach error = $it") }
            )
            .addToSubscribe()
    }

}
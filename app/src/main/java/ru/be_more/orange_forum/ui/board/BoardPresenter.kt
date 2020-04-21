package ru.be_more.orange_forum.ui.board

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class BoardPresenter : MvpPresenter<BoardView>() {

    private var repo = DvachApiRepository
    private lateinit var board :Board
    private var disposable : Disposable? = null
    private var boardId: String = ""
    var listener: ((threadNum: Int, threadTitle: String) -> Unit)? = null

    fun init(boardId: String, listener: ((threadNum: Int, threadTitle: String) -> Unit)?){

        if (listener!=null)
            this.listener = listener

        if (!boardId.isNullOrEmpty())
            this.boardId = boardId

        disposable?.dispose()
        disposable = repo.getBoard(this.boardId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                board = Board("", this.boardId, it)
                viewState.loadBoard(board)
            }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
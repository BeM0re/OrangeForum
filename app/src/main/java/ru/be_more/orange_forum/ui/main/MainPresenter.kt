package ru.be_more.orange_forum.ui.main

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.extentions.disposables
import java.util.*
//import javax.inject.Inject

@InjectViewState
class MainPresenter /*@Inject constructor*/(
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor
): MvpPresenter<MainView>() {

    private var boardId :String = ""
    private lateinit var boardTitle :String
    private var threadNum :Int = 0
    private lateinit var threadTitle :String
    private var currentFragmentTag: String = ""
    private var isBoardAvailable = true

    init {
        setBoard("")
        setThread(0)
    }

    override fun onDestroy() {
        boardInteractor.release()
        threadInteractor.release()
        postInteractor.release()
        super.onDestroy()
    }

    fun getCurrentFragmentTag() = this.currentFragmentTag

    fun setCurrentFragmentTag(tag: String){
        this.currentFragmentTag = tag
    }

    fun getBoardId() = this.boardId

    fun getThreadNum() = this.threadNum

    fun setBoard(boardId: String){
        if (!isBoardAvailable){
            this.boardId = boardId
            setThread(0)
            viewState.showBoardMenuItem()
            viewState.showBoardFragment(true)
        }

        when (boardId){
            "" -> {
                viewState.hideBoardMenuItem()
            }
            this.boardId -> {
                viewState.showBoardMenuItem()
                viewState.showBoardFragment(false)
            }
            else -> {
                this.boardId = boardId
                setThread(0)
                viewState.showBoardMenuItem()
                viewState.showBoardFragment(true)
            }
        }
    }

    fun setThread(threadNum: Int){
        when (threadNum){
            0 -> viewState.hideThreadMenuItem()
            this.threadNum -> {
                viewState.showThreadMenuItem()
                viewState.showThreadFragment(false)
            }
            else -> {
                this.threadNum = threadNum
                viewState.showThreadMenuItem()
                viewState.showThreadFragment(true)
            }
        }

    }

    fun getBoardTitle(): String? = this.boardTitle

    fun getThreadTitle(): String? = this.threadTitle

    @SuppressLint("CheckResult")
    fun downloadThread() { //TODO add progressbar
        threadInteractor.getThread(boardId, threadNum)
            .subscribe (
                {
                    threadInteractor.downloadThread(it, boardId, boardTitle)
                    viewState.turnDownloadedIcon(true)
                },
                { App.showToast("downloading error = $it") }
            )
    }

    @SuppressLint("CheckResult")
    fun deleteThread(boardId: String, threadNum: Int, isDownloadFragmentFrom: Boolean) {
        threadInteractor.deleteThread(boardId, threadNum)
            .subscribe{
                if(!isDownloadFragmentFrom)
                    viewState.turnDownloadedIcon(false)
            }
    }

    fun markFavorite() {
        if (currentFragmentTag == BOARD_TAG)
            boardInteractor.markBoardFavorite(boardId, boardTitle)
        else
            threadInteractor.markThreadFavorite(threadNum, boardId, boardTitle)

        viewState.turnFavoriteIcon(true)
    }

    fun unmarkFavorite() {
        if (currentFragmentTag == BOARD_TAG)
            removeBoardFavoriteMark(boardId, false)
        else
            removeThreadFavoriteMark(boardId, threadNum, false)

        viewState.turnFavoriteIcon(false)
    }

    @SuppressLint("CheckResult")
    fun removeThreadFavoriteMark(boardId: String, threadNum: Int, isFavoriteFragmentFrom: Boolean = false) {
        threadInteractor.unmarkThreadFavorite(boardId, threadNum)
            .subscribe(
                {
                    viewState.refreshFavorite()
                    if(!isFavoriteFragmentFrom)
                        viewState.turnFavoriteIcon(false)
                },
                {Log.d("M_MainPresenter", "unmark favorite error = $it")}
            )
    }

    @SuppressLint("CheckResult")
    private fun removeBoardFavoriteMark(boardId: String, isFavoriteFragmentFrom: Boolean = false) {
        boardInteractor.unmarkBoardFavorite(boardId)
            .subscribe(
                {
                    viewState.refreshFavorite()
                    if(!isFavoriteFragmentFrom)
                        viewState.turnFavoriteIcon(false)
                },
                {Log.d("M_MainPresenter", "unmark favorite error = $it")}
            )
    }

    fun setThreadTitle(title: String) {
        this.threadTitle = title
    }

    fun setBoardTitle(title: String) {
        this.boardTitle = title
    }

    fun setBoardId(boardId: String) {
        this.boardId = boardId
    }

    fun setBoardAvailability(isAvailable: Boolean) {
        this.isBoardAvailable = isAvailable
    }

}
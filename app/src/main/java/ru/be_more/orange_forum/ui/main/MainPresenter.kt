package ru.be_more.orange_forum.ui.main

import android.annotation.SuppressLint
import android.util.Log
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.domain.InteractorContract

class MainPresenter(
    private val boardInteractor : InteractorContract.BoardInteractor,
    private val threadInteractor : InteractorContract.ThreadInteractor,
    private val postInteractor : InteractorContract.PostInteractor,
    private val myViewState: MainView
){

    private var boardId :String = ""
    private lateinit var boardTitle :String
    private var threadNum :Int = 0
    private var currentFragmentTag: String = ""

    fun onDestroy() {
        boardInteractor.release()
        threadInteractor.release()
        postInteractor.release()
    }

    fun getCurrentFragmentTag() = this.currentFragmentTag

    fun setCurrentFragmentTag(tag: String){
        this.currentFragmentTag = tag
    }

    @SuppressLint("CheckResult")
    fun downloadThread() { //TODO add progressbar
        threadInteractor.downloadThread(threadNum, boardId, boardTitle)
            .subscribe (
                { myViewState.turnDownloadedIcon(true) },
                { Log.e("M_MainPresenter","download error = $it")}
            )
    }

    @SuppressLint("CheckResult")
    fun deleteThread(isDownloadFragmentFrom: Boolean) {
        threadInteractor.deleteThread(boardId, threadNum)
            .subscribe{
                if(!isDownloadFragmentFrom)
                    myViewState.turnDownloadedIcon(false)
            }
    }

    fun markFavorite() {
        if (currentFragmentTag == BOARD_TAG)
            boardInteractor.markBoardFavorite(boardId, boardTitle)
                .subscribe()
        else
            threadInteractor.markThreadFavorite(threadNum, boardId, boardTitle)
                .subscribe(
                    {},
                    {Log.e("M_MainPresenter","saving favorite error = $it")}
                )

        myViewState.turnFavoriteIcon(true)
    }

    fun unmarkFavorite() {
        if (currentFragmentTag == BOARD_TAG)
            removeBoardFavoriteMark(boardId, false)
        else
            removeThreadFavoriteMark(boardId, threadNum, false)

        myViewState.turnFavoriteIcon(false)
    }

    @SuppressLint("CheckResult")
    fun removeThreadFavoriteMark(boardId: String, threadNum: Int, isFavoriteFragmentFrom: Boolean = false) {
        threadInteractor.unmarkThreadFavorite(boardId, threadNum)
            .subscribe(
                {
                    myViewState.refreshFavorite()
                    if(!isFavoriteFragmentFrom)
                        myViewState.turnFavoriteIcon(false)
                },
                {Log.d("M_MainPresenter", "unmark favorite error = $it")}
            )
    }

    @SuppressLint("CheckResult")
    private fun removeBoardFavoriteMark(boardId: String, isFavoriteFragmentFrom: Boolean = false) {
        boardInteractor.unmarkBoardFavorite(boardId)
            .subscribe(
                {
                    myViewState.refreshFavorite()
                    if(!isFavoriteFragmentFrom)
                        myViewState.turnFavoriteIcon(false)
                },
                {Log.d("M_MainPresenter", "unmark favorite error = $it")}
            )
    }

}
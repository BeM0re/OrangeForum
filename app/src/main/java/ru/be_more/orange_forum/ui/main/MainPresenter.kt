package ru.be_more.orange_forum.ui.main

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.interactors.ThreadInteractor
import ru.be_more.orange_forum.repositories.DvachDbRepository
import java.util.*
import javax.inject.Inject

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    @Inject
    lateinit var apiRepo : DvachApiRepository
    @Inject
    lateinit var dbRepo : DvachDbRepository
    @Inject
    lateinit var interactor: ThreadInteractor

    private var boardId :String = ""
    private lateinit var boardTitle :String
    private var threadNum :Int = 0
    private lateinit var threadTitle :String
    private var currentFragmentTag: String = ""
    private var isBoardAvailable = true

    private var disposables : LinkedList<Disposable?> = LinkedList()

    init {
        App.getComponent().inject(this)
        setBoard("")
        setThread(0)
        dbRepo.initDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach { it?.dispose() }
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

    fun downloadThread() {
        disposables.add(
            apiRepo.getThread(boardId, threadNum)
                .subscribeOn(Schedulers.io())
                .subscribe ( {
                        dbRepo.saveThread(it, boardId, boardTitle, DvachDbRepository.Purpose.DOWNLOAD)
                    },
                    {
                        App.showToast("downloading error = $it")
                    })
        )
        viewState.turnDownloadedIcon(true)
    }

    fun deleteThread(boardId: String, threadNum: Int, isDownloadFragmentFrom: Boolean) {
        disposables.add( interactor.deleteThread(boardId, threadNum) )
        if(!isDownloadFragmentFrom)
            viewState.turnDownloadedIcon(false)
    }

    fun markFavorite() {
        if (currentFragmentTag == BOARD_TAG)
            interactor.markBoardFavorite(boardId, boardTitle)
        else
            interactor.markThreadFavorite(boardId, threadNum, boardTitle)

        viewState.turnFavoriteIcon(true)
    }

    fun unmarkFavorite() {
        if (currentFragmentTag == BOARD_TAG)
            removeBoardFavoriteMark(boardId, false)
        else
            removeThreadFavoriteMark(boardId, threadNum, false)

        viewState.turnFavoriteIcon(false)
    }

/*    fun markThreadFavorite() {
        interactor.markThreadFavorite(boardId, threadNum, boardTitle)
        viewState.turnFavoriteIcon(true)
    }*/

    fun removeThreadFavoriteMark(boardId: String, threadNum: Int, isFavoriteFragmentFrom: Boolean = false) {
        disposables.add(
            interactor.unmarkThreadFavorite(boardId, threadNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {  },
                    {Log.d("M_MainPresenter", "unmark favorite error = $it")},
                    {
                        viewState.refreshFavorite()
                        if(!isFavoriteFragmentFrom)
                            viewState.turnFavoriteIcon(false)
                    }
                )
        )
    }

/*    fun markBoardFavorite() {
        interactor.markBoardFavorite(boardId, boardTitle)
        viewState.turnFavoriteIcon(true)
    }*/

    private fun removeBoardFavoriteMark(boardId: String, isFavoriteFragmentFrom: Boolean = false) {
        disposables.add(
            interactor.unmarkBoardFavorite(boardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {  },
                    {Log.d("M_MainPresenter", "unmark favorite error = $it")},
                    {
                        viewState.refreshFavorite()
                        if(!isFavoriteFragmentFrom)
                            viewState.turnFavoriteIcon(false)
                    }
                )
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
package ru.be_more.orange_forum.ui.main

import android.util.Log
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.interactors.ThreadInteractor
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.repositories.DvachDbRepository
import ru.be_more.orange_forum.ui.TempFragment
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.download.DownloadFragment
import ru.be_more.orange_forum.ui.favorire.FavoriteFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment
import java.util.*
import javax.inject.Inject

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    @Inject
    lateinit var apiRepo : DvachApiRepository
    @Inject
    lateinit var dbRepo : DvachDbRepository
    @Inject
    lateinit var threadInteractor: ThreadInteractor

    private lateinit var boardId :String
    private lateinit var boardTitle :String
    private var threadNum :Int = 0
    private lateinit var threadTitle :String

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

    private fun setBoard(boardId: String){
        this.boardId = boardId
        if (boardId == "")
            viewState.hideBoardMenuItem()
        else {
            viewState.showBoardMenuItem()
            makeBoardFragment()
        }
    }

    private fun setThread(threadNum: Int){
        this.threadNum = threadNum
        if (threadNum == 0)
            viewState.hideThreadMenuItem()
        else {
            viewState.showThreadMenuItem()
            makeThreadFragment()
        }
    }

    fun getBoardTitle(): String? = this.boardTitle

    fun getThreadTitle(): String? = this.threadTitle

    fun makeCategoryFragment() {

        val fragment=
            CategoryFragment.getCategoryFragment { boardId, title ->
                this.boardTitle = title
                setBoard(boardId)
            }

        viewState.showCategoryFragment(fragment)
    }

    fun makeBoardFragment() {
        val fragment =
            BoardFragment.getBoardFragment({ threadNum, title ->
                this.threadTitle = title
                setThread(threadNum)
            }, this.boardId)
        viewState.showBoardFragment(fragment)
    }

    fun makeThreadFragment() {
        val fragment = ThreadFragment.getThreadFragment(boardId, threadNum)
        viewState.showThreadFragment(fragment)
    }

    fun makeFavoriteFragment() {
        val fragment = FavoriteFragment.getFavoriteFragment({ boardId, threadNum, title ->
            this.boardId = boardId
            this.threadTitle = title
            viewState.setActionBarTitle(title)
            setThread(threadNum)
        },
            {
                    boardId, threadNum -> "" //TODO сделать удаление избранного
            })
        viewState.showFavoriteFragment(fragment)
    }

    fun makeDownloadedFragment() {
        val fragment = DownloadFragment.getDownloadFragment({boardId, threadNum, title ->
            this.boardId = boardId
            this.threadTitle = title
            viewState.setActionBarTitle(title)
            setThread(threadNum)
        },
        {
            boardId, threadNum -> "" //TODO сделать удаление треда
        })
        viewState.showDownloadedFragment(fragment)
    }

    fun makePrefFragment() {
        val fragment = TempFragment()
        viewState.showPrefFragment(fragment)
    }

    fun downloadThread() {
        disposables.add(
            apiRepo.getThread(boardId, threadNum)
                .subscribeOn(Schedulers.io())
                .subscribe ( {
                        dbRepo.saveThread(it, boardId, boardTitle, DvachDbRepository.Purpose.DOWNLOAD)
                    },
                    {
                        App.showToast("Ошибка")
                    })
        )
    }

    fun deleteThread() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun markThreadFavorite() {
        threadInteractor.markThreadFavorite(boardId, threadNum, boardTitle)
        viewState.turnFavoriteIcon(true)
    }

    fun removeFavoriteMark() {
        disposables.add(
            threadInteractor.unmarkThreadFavorite(boardId, threadNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { viewState.turnFavoriteIcon(false) },
                    { Log.d("M_MainPresenter", "main remove favorite error = $it") }
                )
        )
    }


}
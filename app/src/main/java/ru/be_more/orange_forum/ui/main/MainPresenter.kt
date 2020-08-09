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
    private var currentFragmentTag: String = ""

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

    private fun setBoard(boardId: String){
        this.boardId = boardId
        if (boardId == "")
            viewState.hideBoardMenuItem()
        else {
            setThread(0)
            viewState.showBoardMenuItem()
            makeBoardFragment()
        }
    }

    private fun setThread(threadNum: Int){
        when (threadNum ){
            0 -> viewState.hideThreadMenuItem()
            this.threadNum -> {
                viewState.showThreadMenuItem()
                makeThreadFragment(false)
            }
            else -> {
                this.threadNum = threadNum
                viewState.showThreadMenuItem()
                makeThreadFragment(true)
            }
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
        currentFragmentTag = CAT_TAG

    }

    fun makeBoardFragment() {
        val fragment =
            BoardFragment.getBoardFragment({ threadNum, title ->
                this.threadTitle = title
                setThread(threadNum)
            }, this.boardId)

        viewState.showBoardFragment(fragment)
        currentFragmentTag = BOARD_TAG
    }

    fun makeThreadFragment(isNew: Boolean) {
        val fragment = ThreadFragment.getThreadFragment(boardId, threadNum)

        viewState.showThreadFragment(fragment, isNew)
        currentFragmentTag = THREAD_TAG
    }

    fun makeFavoriteFragment() {
        val fragment = FavoriteFragment.getFavoriteFragment({ boardId, threadNum, title ->
            this.boardId = boardId
            this.threadTitle = title
            viewState.setActionBarTitle(title)
            setThread(threadNum)
        },
            { boardId, threadNum ->
                removeFavoriteMark(boardId, threadNum, true)
            })

        viewState.showFavoriteFragment(fragment)
        currentFragmentTag = FAVORITE_TAG
    }

    fun makeDownloadedFragment() {
        val fragment = DownloadFragment.getDownloadFragment({boardId, threadNum, title ->
            this.boardId = boardId
            this.threadTitle = title
            viewState.setActionBarTitle(title)
            setThread(threadNum)
        },
        {
            boardId, threadNum -> deleteThread(boardId, threadNum, true)
        })

        viewState.showDownloadedFragment(fragment)
        currentFragmentTag = DOWNLOAD_TAG
    }

    fun makePrefFragment() {
        val fragment = TempFragment()
        viewState.showPrefFragment(fragment)
        currentFragmentTag = PREF_TAG
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
        viewState.turnDownloadedIcon(true)
    }

    fun deleteThread(boardId: String, threadNum: Int, isDownloadFragmentFrom: Boolean) {
        disposables.add( threadInteractor.deleteThread(boardId, threadNum) )
        if(!isDownloadFragmentFrom)
            viewState.turnDownloadedIcon(false)
    }

    fun markThreadFavorite() {
        threadInteractor.markThreadFavorite(boardId, threadNum, boardTitle)
        viewState.turnFavoriteIcon(true)
    }

    fun removeFavoriteMark(boardId: String, threadNum: Int, isFavoriteFragmentFrom: Boolean = false) {
        disposables.add(
            threadInteractor.unmarkThreadFavorite(boardId, threadNum)
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

}
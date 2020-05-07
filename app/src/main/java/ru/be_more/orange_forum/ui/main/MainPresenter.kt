package ru.be_more.orange_forum.ui.main

import android.util.Log
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.ui.TempFragment
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment
import javax.inject.Inject

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    @Inject
    lateinit var repo : DvachApiRepository

    private lateinit var boardId :String
    private lateinit var boardTitle :String
    private var threadNum :Int = 0
    private lateinit var threadTitle :String

    private var disposable : Disposable? = null

    init {
        setBoard("")
        setThread(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
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
        val fragment = TempFragment()
        viewState.showFavoriteFragment(fragment)
    }

    fun makeDownloadedFragment() {
        val fragment = TempFragment()
        viewState.showDownloadedFragment(fragment)
    }

    fun makePrefFragment() {
        val fragment = TempFragment()
        viewState.showPrefFragment(fragment)
    }

}
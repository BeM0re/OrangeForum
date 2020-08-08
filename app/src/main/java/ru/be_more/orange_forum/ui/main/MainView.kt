package ru.be_more.orange_forum.ui.main

import androidx.fragment.app.Fragment
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment


@StateStrategyType(value = AddToEndStrategy::class)
interface MainView  : MvpView {
    fun hideBoardMenuItem()
    fun hideThreadMenuItem()
    fun showBoardMenuItem()
    fun showThreadMenuItem()
    fun setActionBarTitle(title:String? = "Orange Forum")
    fun showCategoryFragment(categoryFragment: CategoryFragment)
    fun showBoardFragment(boardFragment: BoardFragment)
    fun showThreadFragment(threadFragment: ThreadFragment)
    fun showFavoriteFragment(favoriteFragment: Fragment)
    fun showDownloadedFragment(downloadedFragment: Fragment)
    fun showPrefFragment(prefFragment: Fragment)
    fun turnFavoriteIcon(isFavorite: Boolean)
    fun turnDownloadedIcon(isDownloaded: Boolean)
    fun refreshFavorite()
    fun refreshDownload()
}
package ru.be_more.orange_forum.ui.main

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(value = AddToEndStrategy::class)
interface MainView  : MvpView {
    fun hideBoardMenuItem()
    fun hideThreadMenuItem()
    fun showBoardMenuItem()
    fun showThreadMenuItem()
    fun setActionBarTitle(title:String? = "Orange Forum")
    fun showCategoryFragment()
    fun showBoardFragment(isNew: Boolean)
    fun showThreadFragment(isNew: Boolean)
    fun showFavoriteFragment()
    fun showDownloadedFragment()
    fun showPrefFragment()
    fun turnFavoriteIcon(isFavorite: Boolean)
    fun turnDownloadedIcon(isDownloaded: Boolean)
    fun refreshFavorite()
    fun refreshDownload()
}
package ru.be_more.orange_forum.presentation.screens.main

interface MainView  {
    fun hideBoardMenuItem()
    fun hideThreadMenuItem()
    fun showBoardMenuItem()
    fun showThreadMenuItem()
    fun showPrefFragment()
    fun turnFavoriteIcon(isFavorite: Boolean)
    fun turnDownloadedIcon(isDownloaded: Boolean)
    fun refreshFavorite()
    fun refreshDownload()
}
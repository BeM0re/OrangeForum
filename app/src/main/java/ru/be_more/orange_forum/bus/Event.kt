package ru.be_more.orange_forum.bus

sealed class Event

object BackPressed : Event()
object AppToBeClosed : Event()
object DownloadedThreadEntered : Event()
object UndownloadedThreadEntered : Event()
object FavoriteThreadEntered : Event()
object UnfavoriteThreadEntered : Event()
object ThreadEntered : Event()
object RefreshFavorite: Event()
object RefreshDownload: Event()
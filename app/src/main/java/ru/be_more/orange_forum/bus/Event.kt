package ru.be_more.orange_forum.bus

sealed class Event

object BackPressed : Event()
object AppToBeClosed : Event()
object RefreshFavorite: Event()
object RefreshDownload: Event()
object VideoToBeClosed: Event()
object BoardToBeOpened: Event()
object BoardToBeClosed: Event()
object ThreadToBeOpened: Event()
object ThreadToBeClosed: Event()
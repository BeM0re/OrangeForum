package ru.be_more.orange_forum.presentation.bus

sealed class Event

object BoardToBeOpened: Event()
object BoardToBeClosed: Event()
object ThreadToBeOpened: Event()
object ThreadToBeClosed: Event()
package ru.be_more.orange_forum.presentation.bus
//todo delete
sealed class Event

data object BoardToBeOpened: Event()
data object BoardToBeClosed: Event()
data object ThreadToBeOpened: Event()
data object ThreadToBeClosed: Event()
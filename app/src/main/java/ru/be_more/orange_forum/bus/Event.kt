package ru.be_more.orange_forum.bus

sealed class Event

object BackPressed : Event()
object AppToBeClosed : Event()
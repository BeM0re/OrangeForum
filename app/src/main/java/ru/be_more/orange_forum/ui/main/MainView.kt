package ru.be_more.orange_forum.ui.main

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.BoardThread


@StateStrategyType(value = AddToEndStrategy::class)
interface MainView  : MvpView {
    fun loadThread(thread: BoardThread)
}
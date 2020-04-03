package ru.be_more.orange_forum.ui.board

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category


@StateStrategyType(value = AddToEndStrategy::class)
interface BoardView  : MvpView {
    fun loadBoard(board: Board)
}
package ru.be_more.orange_forum.ui.response

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface ResponseView: MvpView {
    fun closeResponse()

}
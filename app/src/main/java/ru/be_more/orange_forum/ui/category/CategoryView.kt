package ru.be_more.orange_forum.ui.category

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.domain.model.Category


@StateStrategyType(value = AddToEndStrategy::class)
interface CategoryView  : MvpView {
    fun loadCategories(categories: List<Category>)
}
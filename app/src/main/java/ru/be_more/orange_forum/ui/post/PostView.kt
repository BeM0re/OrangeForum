package ru.be_more.orange_forum.ui.post

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.Post


@StateStrategyType(value = AddToEndStrategy::class)
interface PostView  : MvpView {
    fun setPost(post: Post)
}
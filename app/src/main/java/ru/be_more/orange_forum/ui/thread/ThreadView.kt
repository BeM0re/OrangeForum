package ru.be_more.orange_forum.ui.thread

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post


@StateStrategyType(value = AddToEndStrategy::class)
interface ThreadView  : MvpView {
    fun loadThread(thread: BoardThread)
    fun hideResponseFab()
    fun setWebView()
    fun setOnPostClickListener()
    fun displayPost(post: Post)
}
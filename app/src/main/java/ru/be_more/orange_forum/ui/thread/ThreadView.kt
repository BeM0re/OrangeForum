package ru.be_more.orange_forum.ui.thread

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post


@StateStrategyType(value = AddToEndStrategy::class)
interface ThreadView  : MvpView {
    fun loadThread(thread: BoardThread)
    fun hideResponseFab()
    fun setWebView()
    fun setOnPostButtonClickListener()
    fun showPic(attachment: Attachment)
    fun showPost(post: Post)
    fun hideModal()
}
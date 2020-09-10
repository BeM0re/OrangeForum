package ru.be_more.orange_forum.ui.download

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.Post


@StateStrategyType(value = AddToEndStrategy::class)
interface DownloadView  : MvpView {
    fun loadDownloads(boards: List<Board>)
    fun loadDownloads()
    fun showPic(attachment: Attachment)
    fun showPost(post: Post)
    fun hideModal()
    fun showToast(message: String)
}
package ru.be_more.orange_forum.ui.favorire

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.model.Post
import java.security.MessageDigest


@StateStrategyType(value = AddToEndStrategy::class)
interface FavoriteView  : MvpView {
//    fun loadFavorites(boards: List<Board>)
    fun loadFavorites()
    fun showPic(attachment: Attachment)
    fun showPost(post: Post)
    fun hideModal()
    fun showToast(message: String)
}
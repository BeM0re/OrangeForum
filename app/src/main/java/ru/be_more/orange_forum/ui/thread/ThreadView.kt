package ru.be_more.orange_forum.ui.thread

import ru.be_more.orange_forum.domain.model.Attachment
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

interface ThreadView {
    fun loadThread(thread: BoardThread)
    fun setThreadMarks (isDownloaded: Boolean, isFavorite: Boolean)
    fun hideResponseFab()
    fun showResponseForm()
    fun showPic(attachment: Attachment)
    fun showPost(post: Post)
    fun hideModal()
    fun showToast(message: String)
}
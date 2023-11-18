package ru.be_more.orange_forum.presentation.screens.base

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.BoardSetting
import ru.be_more.orange_forum.presentation.composeViews.ModalContentDialogInitArgs
import ru.be_more.orange_forum.presentation.data.ImageInitArgs
import ru.be_more.orange_forum.presentation.data.PostInitArgs
import ru.be_more.orange_forum.presentation.data.TextLinkArgs
import java.util.*

abstract class BaseModalContentViewModel(
    protected open val boardId: String,
    protected open val postInteractor: InteractorContract.PostInteractor,
    protected open val replyInteractor: InteractorContract.ReplyInteractor,
) : BaseViewModel() {

    //stack returns exception on empty, therefore using list as stack
    private val modalStack: LinkedList<ModalContentDialogInitArgs> = LinkedList()

    var modalContent by mutableStateOf<ModalContentDialogInitArgs?>(null)
        private set

    abstract val boardSetting: BoardSetting

    protected fun onPicClicked(file: AttachedFile) =
        pushModelContent(
            ModalContentDialogInitArgs(
                modalArgs = ImageInitArgs(file),
                onBack = ::closeModal,
                onClose = ::clearModal,
            )
        )

    protected fun onTextLinkClicked(linkArgs: TextLinkArgs) {
        when (linkArgs) {
            is TextLinkArgs.DomesticPostLink ->
                showPostModel(boardId, linkArgs.threadNum, linkArgs.postId)

            is TextLinkArgs.ExternalLink -> { /*todo*/ }
        }
    }

    private fun showPostModel(boardId: String, threadNum: Int, postId: Int) {
        postInteractor.getPost(boardId, threadNum, postId)
            .defaultThreads()
            .subscribe(
                { post ->
                    pushModelContent(
                        ModalContentDialogInitArgs(
                            modalArgs = PostInitArgs(
                                post = post,
                                onPicClick = ::onPicClicked,
                                onTextLinkClick = ::onTextLinkClicked,
                            ),
                            onBack = ::closeModal,
                            onClose = ::clearModal,
                        )
                    )
                },
                { Log.e("BaseModalContentViewModel", "BaseModalContentViewModel.showPostModel = \n$it") }
            )
            .addToSubscribe()

    }

    private fun pushModelContent(content: ModalContentDialogInitArgs) {
        modalContent?.let { modalStack.push(it) }
        modalContent = content
    }

    protected fun closeModal() {
        modalContent = modalStack.removeFirstOrNull()
    }

    private fun clearModal() {
        modalStack.clear()
        modalContent = null
    }
}
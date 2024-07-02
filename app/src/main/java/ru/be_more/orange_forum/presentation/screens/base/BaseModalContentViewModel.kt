package ru.be_more.orange_forum.presentation.screens.base

import kotlinx.coroutines.flow.MutableStateFlow
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.model.model.AttachedFile
import ru.be_more.model.model.BoardSetting
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import ru.be_more.model.model.Post
import ru.be_more.ui.composeViews.ModalContentDialogInitArgs
import ru.be_more.ui.composeViews.initArgs.ImageInitArgs
import ru.be_more.ui.composeViews.initArgs.PostInitArgs
import ru.be_more.ui.composeViews.initArgs.TextLinkArgs
import java.util.*

abstract class BaseModalContentViewModel(
    protected open val boardId: String,
    protected open val postInteractor: InteractorContract.PostInteractor,
    protected open val replyInteractor: InteractorContract.ReplyInteractor,
) : BaseViewModel() {

    //stack returns exception on empty, therefore using list as stack
    private val modalStack: LinkedList<ModalContentDialogInitArgs> = LinkedList()

    var modalContent = MutableStateFlow<ModalContentDialogInitArgs?>(null)

    abstract val boardSetting: BoardSetting

    protected fun onPicClicked(file: AttachedFile) =
        pushModelContent(
            ModalContentDialogInitArgs(
                modalArgs = ImageInitArgs(file),
                onBack = ::closeModal,
                onClose = ::clearModal,
            )
        )

    protected fun onTextLinkClicked(imageboardType: ImageboardType, linkArgs: TextLinkArgs) {
        when (linkArgs) {
            is TextLinkArgs.DomesticPostLink ->
                showPostModel(imageboardType, boardId, linkArgs.threadNum, linkArgs.postId)

            is TextLinkArgs.ExternalLink -> { /* todo */ }
        }
    }

    private fun showPostModel(imageboardType: ImageboardType, boardId: String, threadNum: Int, postId: Int) =
        runCoroutine("showPostModel") {
            pushModelContent(
                ModalContentDialogInitArgs(
                    modalArgs = PostInitArgs(
                        post = postInteractor.getPost(imageboardType, boardId, threadNum, postId),
                        onPicClick = ::onPicClicked,
                        onTextLinkClick = { onTextLinkClicked(imageboardType, it) },
                        onPostNumClick = { replyToPost(imageboardType, it) }
                    ),
                    onBack = ::closeModal,
                    onClose = ::clearModal,
                )
            )
        }

    private fun pushModelContent(content: ModalContentDialogInitArgs) =
        runCoroutine("pushModelContent") {
            modalContent.let { modalStack.push(it.value) }
            modalContent.emit(content)
        }

    private fun closeModal() =
        runCoroutine("closeModal") {
            modalContent.emit(modalStack.removeFirstOrNull())
        }

    private fun clearModal() =
        runCoroutine("clearModal") {
            modalStack.clear()
            modalContent.emit(null)
        }

    protected fun replyToPost(imageboardType: ImageboardType, post: Post) {
        navigateToReply(
            imageboardType = imageboardType,
            boardId = post.boardId,
            threadNum = post.threadNum,
            additionalString = ">>${post.id}",
        )
    }
}
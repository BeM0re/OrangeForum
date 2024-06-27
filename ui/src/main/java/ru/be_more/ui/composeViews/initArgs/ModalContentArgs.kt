package ru.be_more.ui.composeViews.initArgs

import ru.be_more.model.model.AttachedFile
import ru.be_more.model.model.Post

sealed interface ModalContentArgs

data class ImageInitArgs(
    val file: AttachedFile
) : ModalContentArgs

data class PostInitArgs(
    val post: Post,
    val onPicClick: (AttachedFile) -> Unit,
    val onTextLinkClick: (TextLinkArgs) -> Unit,
    val onPostNumClick: (Post) -> Unit,
) : ModalContentArgs
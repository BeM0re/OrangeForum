package ru.be_more.orange_forum.presentation.composeViews.initArgs

import ru.be_more.orange_forum.domain.model.AttachedFile
import ru.be_more.orange_forum.domain.model.Post

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
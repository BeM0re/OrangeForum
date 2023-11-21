package ru.be_more.orange_forum.presentation.composeViews.initArgs

sealed interface TextLinkArgs {

    data class DomesticPostLink(
        val threadNum: Int,
        val postId: Int,
    ) : TextLinkArgs

    data class ExternalLink(
        val url: String,
    ) : TextLinkArgs
}
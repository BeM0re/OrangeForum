package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.presentation.model.AnnotatedTextTag
import ru.be_more.orange_forum.presentation.model.AnnotatedTextTag.domesticPostId
import ru.be_more.orange_forum.presentation.model.AnnotatedTextTag.domesticThreadNum
import ru.be_more.orange_forum.presentation.model.TextColors
import ru.be_more.orange_forum.presentation.composeViews.initArgs.TextLinkArgs

@Composable
fun ReplyTextView(
    post: Post,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.W400,
    color: TextColors = TextColors(
        regularTextColor = MaterialTheme.colorScheme.onPrimary,
        urlColor = MaterialTheme.colorScheme.tertiary,
        quoteColor = MaterialTheme.colorScheme.onPrimary,
    ),
    onTextClick: (TextLinkArgs) -> Unit,
) {
    val text: AnnotatedString = buildAnnotatedString {
        post.replies.forEachIndexed { index, reply ->

            val start = length
            append(">${reply}")
            val end = length

            if (index < post.replies.size - 1)
                append(", ")

            addStringAnnotation(
                tag = domesticThreadNum,
                annotation = post.threadNum.toString(),
                start = start,
                end = end,
            )
            addStringAnnotation(
                tag = domesticPostId,
                annotation = reply.toString(),
                start = start,
                end = end,
            )
            val spanStyle = SpanStyle(
                color = color.urlColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
            )

            addStyle(spanStyle, start, end)
        }
    }

    val textStyle = TextStyle(
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        color = color.regularTextColor,
    )

    ClickableText(
        text = text,
        style = textStyle,
        modifier = modifier
    ) { charPos ->
        val annotation = text.getStringAnnotations(charPos, charPos)
        val url = annotation.firstOrNull { it.tag == AnnotatedTextTag.externalUrl }?.item
        val threadNum = annotation.firstOrNull { it.tag == domesticThreadNum }?.item?.toIntOrNull()
        val postId = annotation.firstOrNull { it.tag == domesticPostId }?.item?.toIntOrNull()

        when {
            threadNum != null && postId != null -> onTextClick(
                TextLinkArgs.DomesticPostLink(
                    threadNum = threadNum,
                    postId = postId
                )
            )

            url != null -> onTextClick(
                TextLinkArgs.ExternalLink(url)
            )
        }
    }
}
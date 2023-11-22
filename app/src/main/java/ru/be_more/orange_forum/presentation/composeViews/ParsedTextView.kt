package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dmytroshuba.dailytags.core.simple.SimpleMarkupParser
import com.dmytroshuba.dailytags.core.simple.render
import ru.be_more.orange_forum.presentation.data.AnnotatedTextTag.domesticPostId
import ru.be_more.orange_forum.presentation.data.AnnotatedTextTag.domesticThreadNum
import ru.be_more.orange_forum.presentation.data.AnnotatedTextTag.externalUrl
import ru.be_more.orange_forum.presentation.data.TextColors
import ru.be_more.orange_forum.presentation.composeViews.initArgs.TextLinkArgs
import ru.be_more.orange_forum.presentation.theme.greenText
import ru.be_more.orange_forum.utils.ParseHtml
import ru.be_more.orange_forum.utils.ParseHtml.processHtmlTrash

@Composable
fun ParsedTextView(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = 16.sp,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    fontWeight: FontWeight = FontWeight.W400,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    isExpandable: Boolean = false,
    onTextClick: (TextLinkArgs) -> Unit,
) {
    var currentLines by rememberSaveable {
        mutableIntStateOf(maxLines)
    }
    val processingText = processHtmlTrash(text)

    val colors = TextColors(
        regularTextColor = MaterialTheme.colorScheme.onPrimary,
        urlColor = MaterialTheme.colorScheme.tertiary,
        quoteColor = greenText
    )

    val rules = ParseHtml.getRules(colors)
    val parser = SimpleMarkupParser()
    val content = parser
        .parse(processingText, rules)
        .render()
        .toAnnotatedString()

    val textStyle = TextStyle(
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        color = color,
    )

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.tertiary,
        backgroundColor = MaterialTheme.colorScheme.secondary
    )


    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        SelectionContainer {
            ClickableText(
                text = content,
                style = textStyle,
                maxLines = currentLines,
                overflow = overflow,
                modifier = modifier
            ) { charPos ->
                val annotation = content.getStringAnnotations(charPos, charPos)
                val url = annotation.firstOrNull { it.tag == externalUrl }?.item
                val threadNum = annotation.firstOrNull { it.tag == domesticThreadNum }?.item?.toIntOrNull()
                val postId = annotation.firstOrNull { it.tag == domesticPostId }?.item?.toIntOrNull()

                when {
                    threadNum != null && postId != null -> onTextClick(
                        TextLinkArgs.DomesticPostLink(threadNum, postId)
                    )

                    url != null -> onTextClick(
                        TextLinkArgs.ExternalLink(url)
                    )

                    else -> {
                        when {
                            isExpandable && currentLines != maxLines ->
                                currentLines = maxLines
                            isExpandable && currentLines == maxLines ->
                                currentLines = Int.MAX_VALUE
                        }
                    }
                }
            }
        }
    }


}
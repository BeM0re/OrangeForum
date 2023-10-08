package ru.be_more.orange_forum.presentation.composeViews

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dmytroshuba.dailytags.core.simple.SimpleMarkupParser
import com.dmytroshuba.dailytags.core.simple.render
import com.dmytroshuba.dailytags.markdown.styles.Links
import ru.be_more.orange_forum.utils.ParseHtml
import ru.be_more.orange_forum.utils.ParseHtml.processHtmlTrash

@Composable
fun CommentTextView(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = 16.sp,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    isExpandedDefault: Boolean = false
) {
    var isExpanded by rememberSaveable { mutableStateOf(isExpandedDefault) }
    val processingText = processHtmlTrash(text)

//    val parsedText = Jsoup.parse(text).text()

    val rules = ParseHtml.rules
    val parser = SimpleMarkupParser()
    val content = parser
        .parse(processingText, rules)
        .render()
        .toAnnotatedString()

    val textStyle = TextStyle(
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = color,
    )


    ClickableText(
        text = content,
        style = textStyle,
        maxLines = if (!isExpanded) Int.MAX_VALUE else 4,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
//            .clickable { isExpanded = !isExpanded }
    ) { charPos ->
        val annotation = content.getStringAnnotations(charPos, charPos)
        val url = annotation.firstOrNull { it.tag == Links.Properties.KEY_URL }

    }


}


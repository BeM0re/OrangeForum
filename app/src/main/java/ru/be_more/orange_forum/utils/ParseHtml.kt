package ru.be_more.orange_forum.utils

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import com.dmytroshuba.dailytags.core.simple.Decoration
import com.dmytroshuba.dailytags.core.simple.OnMatch
import com.dmytroshuba.dailytags.core.simple.Rule.Companion.applyCustomSettings
import com.dmytroshuba.dailytags.core.simple.toRule
import com.dmytroshuba.dailytags.markdown.MarkdownStyles
import com.dmytroshuba.dailytags.markdown.rules.HtmlRules
import com.dmytroshuba.dailytags.markdown.rules.PATTERN_TEXT
import com.dmytroshuba.dailytags.markdown.styles.Links
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.be_more.orange_forum.domain.model.Reply
import java.util.*
import java.util.regex.Pattern

object ParseHtml{

    fun parse(htmlText: String): String{

        if (htmlText.isEmpty()) return ""

        var doc: Document = Jsoup.parse(htmlText)



        return htmlText
    }

    fun findReply(from: Int, html: String): List<Reply>{

        val doc: Document = Jsoup.parse(html)
        val elements = doc.select(".post-reply-link")
        val replies = LinkedList<Int>()

        elements.forEach { element ->
            try {
                replies.add(element.attr("data-num").toInt())
            }
            catch (error: NumberFormatException){
                Log.e("M_ParseHtml", "cant parse reply post num: $error")
            }
        }

       return replies.map { Reply(from, it) }
    }

    fun processHtmlTrash(text: String): String =
        text
            .replace("<br>", "\n")
            .replace("<p>", "")
            .replace("</p>", "")
            .replace("&nbsp;", "\u00A0")
            .replace("&#47;", "/")

    const val REGEX_PLAIN_TEXT = "([\\s\\S]+?)"

    private val patternSpoiler: Pattern = Pattern.compile("""^<span class="spoiler">(.*?)</span>""")
    private val patternLink: Pattern = Pattern.compile("""^<a href=["'](.*?)["'].*?>([\s\S]*?)</a>""")

    val dvachLinkDecoration: Decoration = Decoration(
        spanStyle = SpanStyle(
            color = Color.Magenta,
        ),
        properties = mutableMapOf(),
    )

    val onMatch: OnMatch = { matcher, decoration ->
        val key = Links.Properties.KEY_URL
        val url = matcher.group(1) ?: ""
        decoration.properties?.set(key, url)
        true
    }

    val rules = listOf(
        patternSpoiler.toRule(SpanStyle(background = Color.Gray)),
//        patternLink.toRule(SpanStyle(background = Color.Green)),
        patternLink.toRule(dvachLinkDecoration)
            .applyCustomSettings(2, onMatch = onMatch),
        PATTERN_TEXT.toRule()
    )
}
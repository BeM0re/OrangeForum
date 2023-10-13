package ru.be_more.orange_forum.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration.Companion.LineThrough
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import com.dmytroshuba.dailytags.core.simple.Decoration
import com.dmytroshuba.dailytags.core.simple.OnMatch
import com.dmytroshuba.dailytags.core.simple.Rule
import com.dmytroshuba.dailytags.core.simple.Rule.Companion.applyCustomSettings
import com.dmytroshuba.dailytags.core.simple.toRule
import com.dmytroshuba.dailytags.markdown.rules.PATTERN_TEXT
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.be_more.orange_forum.domain.model.Reply
import ru.be_more.orange_forum.presentation.data.AnnotatedTextTag.domesticPostId
import ru.be_more.orange_forum.presentation.data.AnnotatedTextTag.domesticThreadNum
import ru.be_more.orange_forum.presentation.data.AnnotatedTextTag.externalUrl
import ru.be_more.orange_forum.presentation.data.TextColors
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
        val replies = LinkedList<Reply>()

        elements.forEach { element ->
            try {
                replies.add(
                    Reply(
                        from = from,
                        to = element.attr("data-num").toInt()
                    )
                )
            }
            catch (error: NumberFormatException){
                Log.e("M_ParseHtml", "cant parse reply post num: $error")
            }
        }

       return replies
    }

    fun processHtmlTrash(text: String): String =
        text
            .replace("<br>", "\n")
            .replace("<p>", "")
            .replace("</p>", "")
            .replace("&nbsp;", "\u00A0")
            .replace("&#47;", "/")
            .replace("&gt;", ">")
            .replace("&#37;", "%")
            .replace("&quot", "\"")

    const val REGEX_PLAIN_TEXT = "([\\s\\S]+?)"

    private val patternLink: Pattern = Pattern.compile("""^<a href=["'](.*?)["'].*?>([\s\S]*?)</a>""")
    private val patternDomesticLink: Pattern = Pattern.compile(
        """^<a href=["'](.*?)["'][\s\S]*?data-thread=["'](.*?)["'][\s\S]*?data-num=["'](.*?)["'].*?>([\s\S]*?)</a>"""
    )
    private val patternSpoiler: Pattern = Pattern.compile("""^<span class="spoiler">(.*?)</span>""")
    private val patternBold: Pattern = Pattern.compile("""^<strong>([\s\S]*?)</strong>""")
    private val patternBoldShort: Pattern = Pattern.compile("""^<b>([\s\S]*?)</b>""")
    private val patternItalic: Pattern = Pattern.compile("""^<em>([\s\S]*?)</em>""")
    private val patternItalicShort: Pattern = Pattern.compile("""^<i>([\s\S]*?)</i>""")
    private val patternQuote: Pattern = Pattern.compile("""^<span class="unkfunc">([\s\S]*?)</span>""")
    private val patternUnderline: Pattern = Pattern.compile("""^<span class="u">([\s\S]*?)</span>""")
    private val patternAboveLine: Pattern = Pattern.compile("""^<span class="o">([\s\S]*?)</span>""")
    private val patternStrikethrough: Pattern = Pattern.compile("""^<span class="s">([\s\S]*?)</span>""")
    private val patternAboveText: Pattern = Pattern.compile("""^<sup>([\s\S]*?)</sup>""")
    private val patternLowerText: Pattern = Pattern.compile("""^<sub>([\s\S]*?)</sub>""")


    fun getRules(colors: TextColors): List<Rule> {

        val dvachLinkDecoration = Decoration(
            spanStyle = SpanStyle(
                color = colors.urlColor,
            ),
            properties = mutableMapOf(),
        )

        val onLinkMatch: OnMatch = { matcher, decoration ->
            val url = matcher.group(1) ?: ""
            decoration.properties?.set(externalUrl, url)
            true
        }

        val onDomesticLinkMatch: OnMatch = { matcher, decoration ->
            //group 1 - usual url
            val threadNum = matcher.group(2) ?: ""
            val postId = matcher.group(3) ?: ""
            decoration.properties?.set(domesticThreadNum, threadNum)
            decoration.properties?.set(domesticPostId, postId)
            true
        }

        return listOf(
            patternDomesticLink.toRule(dvachLinkDecoration)
                .applyCustomSettings(4, onMatch = onDomesticLinkMatch),
            patternLink.toRule(dvachLinkDecoration)
                .applyCustomSettings(2, onMatch = onLinkMatch),
            patternSpoiler.toRule(SpanStyle(background = Color.Gray)),
            patternBold.toRule(SpanStyle(fontWeight = W600)),
            patternBoldShort.toRule(SpanStyle(fontWeight = W600)),
            patternItalic.toRule(SpanStyle(fontStyle = FontStyle.Italic)),
            patternItalicShort.toRule(SpanStyle(fontStyle = FontStyle.Italic)),
            patternQuote.toRule(SpanStyle(color = colors.quoteColor)),
            patternQuote.toRule(SpanStyle(color = colors.quoteColor)),
            patternUnderline.toRule(SpanStyle(textDecoration = Underline)),
//            patternAboveLine.toRule(SpanStyle(color = colors.quoteColor)),
            patternStrikethrough.toRule(SpanStyle(textDecoration = LineThrough)),
            patternAboveText.toRule(SpanStyle(baselineShift = BaselineShift.Superscript)),
            patternLowerText.toRule(SpanStyle(baselineShift = BaselineShift.Subscript)),
            PATTERN_TEXT.toRule()
        )
    }


}
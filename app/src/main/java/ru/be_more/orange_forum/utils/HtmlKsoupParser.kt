package ru.be_more.orange_forum.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import ru.be_more.orange_forum.presentation.model.TextColors
import java.util.*

object HtmlKsoupParser {

    fun parseHtml(html: String, colors: TextColors): AnnotatedString {

        val stringBuilder = AnnotatedString.Builder()
        val tagStack = Stack<Pair<String, Map<String, String>>>()
        var text = ""

        val handler = KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, isImplied ->
                tagStack.push(name to attributes)
                println("< "+name+"  attrs = "+attributes)
            }
            .onAttribute { name, value, quote ->

                println("attr = "+name+" value = "+value)
            }
            .onText {
                text = it
                println("text = "+text)
            }
            .onEnd {
                println("end")

            }
            .build()

        val ksoupHtmlParser = KsoupHtmlParser(handler = handler)
        ksoupHtmlParser.write(html)
        ksoupHtmlParser.end()
        return stringBuilder.toAnnotatedString()
    }

    private fun parseTag(
        tag: String,
        attributes: Map<String, String>,
        text: String,
        builder: AnnotatedString.Builder
    ) {
        ""
        when(tag) {
            "br" -> builder.appendLine()
            else -> builder.append(text)
        }
    }
}
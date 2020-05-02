package ru.be_more.orange_forum.utils

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

object ParseHtml{

    fun parse(htmlText: String): String{

        if (htmlText.isEmpty()) return ""

        var doc: Document = Jsoup.parse(htmlText)



        return htmlText
    }

    fun findReply(html: String): List<Int>{

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

       return replies
    }
}
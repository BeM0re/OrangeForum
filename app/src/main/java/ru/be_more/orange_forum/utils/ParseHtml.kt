package ru.be_more.orange_forum.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ParseHtml{

    fun parse(htmlText: String): String{

        if (htmlText.isEmpty()) return ""

        var doc: Document = Jsoup.parse(htmlText)



        return htmlText
    }

}
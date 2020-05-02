package ru.be_more.orange_forum

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test
import java.util.logging.Logger

class HtmlParseTest {
    @Test
    fun parseTest() {

        val styledHtml = " \"<p>Шаблон анкеты:<br>1. Город/страна.<br><b>2. Возраст, рост, вес, типаж (обычнота/дрищ/кочка/всратан).</b><br>3. Ориентация.<br>4. Роль в постели и фетиши (если имеются).<br>5. Чем занимаешься (работаешь/учишься/сидишь дома/рассуждаешь о цели бесцельной жизни).<br>6. Хобби и увлечения, цели в жизни (если имеются).<br>7. Отношение и пристрастия к алкоголю/табаку/веществам.<br>8. Кого, для чего, зачем и почему ищешь.<br><b>9. Контакты.</b></p><p><b>Тред только для анкет. <span style=\\\"background-color: rgb(255, 0, 0);\\\">Реклама конференций запрещена</span>.\\\\r\\\\n Обсуждения и флуд будут удаляться, а их авторы - <span style=\\\"background-color: rgb(255, 0, 0);\\\">отправляться в \\\\r\\\\nбан</span>. Если вы не указали свой возраст, ваш пост будет удалён, как и \\\\r\\\\nвсе посты без контактов или с отсутствующими пунктами 2 и 9..<br></b></p>\""

        val linkedHtml = "Прошлый<span class=\"spoiler\"><a href=\"/ga/res/1640099.html#1640099\" class=\"post-reply-link\" data-thread=\"1640099\" data-num=\"1640099\">>>1640099 (OP)</a></span>"

        val doc: Document = Jsoup.parse(linkedHtml)
        log.info("\"doc = $doc \n\n")

        val spoilElements = doc.select("span")
//        log.info("elements = $elements \n\n")


        val linkElements = doc.select(".spoiler")
        log.info("elements = $linkElements \n\n")

//        ParseHtml.parse(html)
    }


    companion object {
        val log = Logger.getLogger(HtmlParseTest::class.java.name)
    }
}
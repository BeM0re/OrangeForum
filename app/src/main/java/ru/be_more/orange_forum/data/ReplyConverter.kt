package ru.be_more.orange_forum.data

import androidx.room.TypeConverter
import java.util.*


class ReplyConverter {
    @TypeConverter
    fun fromReplies(replies: List<Int?>): String {
        var result =""
        replies.forEach { result+="$it," }
        return result
    }

    @TypeConverter
    fun toReplies(data: String): Stack<Int> {
        val result: Stack<Int> = Stack()
        data.split(",").toList().forEach { result.add(it.toInt()) }
        return result
    }
}
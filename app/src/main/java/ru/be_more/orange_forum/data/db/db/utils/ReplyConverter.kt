package ru.be_more.orange_forum.data.db.db.utils

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

        if (data.isNotEmpty()){
            val replies = data.substring(0, data.length-1)
            replies.split(",").forEach {
                result.add(it.toInt())
            }
        }
        return result
    }
}
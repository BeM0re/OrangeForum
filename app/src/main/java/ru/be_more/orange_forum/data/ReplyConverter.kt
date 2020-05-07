package ru.be_more.orange_forum.data

import androidx.room.TypeConverter


class ReplyConverter {
    @TypeConverter
    fun fromReplies(replies: List<Int?>): String {
        var result =""
        replies.forEach { result+="$it," }
        return result
    }

    @TypeConverter
    fun toReplies(data: String): List<Int> {
        val strings = data.split(",").toList()
        return strings.map{ it.toInt() }
    }
}
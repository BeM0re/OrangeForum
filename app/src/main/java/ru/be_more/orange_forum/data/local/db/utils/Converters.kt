package ru.be_more.orange_forum.data.local.db.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.domain.model.Icon
import java.util.*

class Converters {
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

/*    @TypeConverter
    fun fromStringList(strings: List<String>): String {
        return Gson().toJson(strings)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        if (json == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun fromIcons(strings: List<Icon>): String {
        return Gson().toJson(strings)
    }

    @TypeConverter
    fun toIcons(json: String?): List<Icon> {
        if (json == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<Icon>>() {}.type
        return Gson().fromJson(json, listType)
    }*/
}
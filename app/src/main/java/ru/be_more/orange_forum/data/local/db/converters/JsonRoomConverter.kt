package ru.be_more.orange_forum.data.local.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.entities.StoredThread

//todo review and remove redundant
object JsonRoomConverter {
    @TypeConverter
    @JvmStatic
    fun fromBoard(board: StoredBoard): String {
        return Gson().toJson(board)
    }

    @TypeConverter
    @JvmStatic
    fun toBoard(json: String): StoredBoard {
        return Gson().fromJson(json, StoredBoard::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromThread(thread: StoredThread): String {
        return Gson().toJson(thread)
    }

    @TypeConverter
    @JvmStatic
    fun toThread(json: String): StoredThread {
        return Gson().fromJson(json, StoredThread::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromThreads(threads: List<StoredThread>): String {
        return Gson().toJson(threads)
    }

    @TypeConverter
    @JvmStatic
    fun toThreads(json: String?): List<StoredThread> {
        if (json == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<StoredThread>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromPost(post: StoredPost): String {
        return Gson().toJson(post)
    }

    @TypeConverter
    @JvmStatic
    fun toPost(json: String): StoredPost {
        return Gson().fromJson(json, StoredPost::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromPosts(posts: List<StoredPost>): String {
        return Gson().toJson(posts)
    }

    @TypeConverter
    @JvmStatic
    fun toPosts(json: String?): List<StoredPost> {
        if (json == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<StoredPost>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromFile(file: StoredFile): String {
        return Gson().toJson(file)
    }

    @TypeConverter
    @JvmStatic
    fun toFile(json: String): StoredFile {
        return Gson().fromJson(json, StoredFile::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromFiles(files: List<StoredFile>): String {
        return Gson().toJson(files)
    }

    @TypeConverter
    @JvmStatic
    fun toFiles(json: String?): List<StoredFile> {
        if (json == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<StoredFile>>() {}.type
        return Gson().fromJson(json, listType)
    }
}
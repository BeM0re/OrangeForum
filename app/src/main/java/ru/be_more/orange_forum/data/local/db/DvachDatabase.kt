package ru.be_more.orange_forum.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.dao.PostDao
import ru.be_more.orange_forum.data.local.db.dao.ThreadDao
import ru.be_more.orange_forum.data.local.db.entities.*
import ru.be_more.orange_forum.data.local.db.utils.ReplyConverter

@Database(
    entities = [
        StoredCategory::class,
        StoredBoard::class,
        StoredThread::class,
        StoredPost::class,
        StoredFile::class
    ],
    version = 1,
    exportSchema = false)

@TypeConverters(ReplyConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun threadDao(): ThreadDao
    abstract fun postDao(): PostDao
}
package ru.be_more.orange_forum.data.db.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.db.db.dao.DvachDao
import ru.be_more.orange_forum.data.db.db.entities.*
import ru.be_more.orange_forum.data.db.db.utils.ReplyConverter

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
    abstract fun boardDao(): DvachDao
    abstract fun threadDao(): ThreadDao
    abstract fun postDao(): PostDao
}
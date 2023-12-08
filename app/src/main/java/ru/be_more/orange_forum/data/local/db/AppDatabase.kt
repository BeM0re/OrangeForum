package ru.be_more.orange_forum.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.be_more.orange_forum.data.local.db.converters.JsonRoomConverter
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.dao.CategoryDao
import ru.be_more.orange_forum.data.local.db.dao.PostDao
import ru.be_more.orange_forum.data.local.db.dao.ThreadDao
import ru.be_more.orange_forum.data.local.db.entities.*
import ru.be_more.orange_forum.data.local.db.utils.Converters

@Database(
    entities = [
        StoredCategory::class,
        StoredBoard::class,
        StoredThread::class,
        StoredPost::class,
    ],
    version = 17,
    exportSchema = false
)

@TypeConverters(Converters::class, JsonRoomConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun boardDao(): BoardDao
    abstract fun threadDao(): ThreadDao
    abstract fun postDao(): PostDao
}
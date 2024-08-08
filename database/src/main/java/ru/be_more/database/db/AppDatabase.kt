package ru.be_more.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.be_more.database.db.converters.JsonRoomConverter
import ru.be_more.database.db.dao.BoardDao
import ru.be_more.database.db.dao.CategoryDao
import ru.be_more.database.db.dao.PostDao
import ru.be_more.database.db.dao.ThreadDao
import ru.be_more.database.db.entities.StoredBoard
import ru.be_more.database.db.entities.StoredCategory
import ru.be_more.database.db.entities.StoredPost
import ru.be_more.database.db.entities.StoredThread
import ru.be_more.database.db.utils.Converters

@Database(
    entities = [
        StoredCategory::class,
        StoredBoard::class,
        StoredThread::class,
        StoredPost::class,
    ],
    version = 18,
    exportSchema = false
)

@TypeConverters(Converters::class, JsonRoomConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun boardDao(): BoardDao
    abstract fun threadDao(): ThreadDao
    abstract fun postDao(): PostDao
}
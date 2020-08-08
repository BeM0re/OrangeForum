package ru.be_more.orange_forum.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

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
    abstract fun dvachDao(): DvachDao
}
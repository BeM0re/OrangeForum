package ru.be_more.orange_forum.data

import androidx.room.Database
import androidx.room.RoomDatabase

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


abstract class AppDatabase : RoomDatabase() {
    abstract fun dvachDao(): DvachDao
}
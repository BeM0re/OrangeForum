package ru.be_more.orange_forum.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.data.local.db.AppDatabase
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun create(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(appDatabase: AppDatabase): DvachDao = appDatabase.dao()
}
package ru.be_more.orange_forum.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.be_more.database.db.AppDatabase
import ru.be_more.database.db.dao.BoardDao
import ru.be_more.database.db.dao.CategoryDao
import ru.be_more.database.db.dao.PostDao
import ru.be_more.database.db.dao.ThreadDao
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        context: Context
    ): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "database"
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao {
        return db.categoryDao()
    }

    @Singleton
    @Provides
    fun provideBoardDao(db: AppDatabase): BoardDao {
        return db.boardDao()
    }

    @Singleton
    @Provides
    fun provideThreadDao(db: AppDatabase): ThreadDao {
        return db.threadDao()
    }

    @Singleton
    @Provides
    fun providePostDao(db: AppDatabase): PostDao {
        return db.postDao()
    }
}
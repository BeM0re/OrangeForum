package ru.be_more.orange_forum.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.data.local.db.AppDatabase
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.dao.CategoryDao
import ru.be_more.orange_forum.data.local.db.dao.PostDao
import ru.be_more.orange_forum.data.local.db.dao.ThreadDao
import ru.be_more.orange_forum.data.local.storage.LocalStorageImpl
import ru.be_more.orange_forum.domain.contracts.StorageContract
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

//todo move
@Module
interface StorageModule {
    @Binds
    fun bindLocalStorage(localStorage: LocalStorageImpl): StorageContract.LocalStorage
}
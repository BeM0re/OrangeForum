package ru.be_more.orange_forum.di.modules

import android.content.Context
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
import ru.be_more.orange_forum.data.local.db.AppDatabase
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
//import javax.inject.Singleton

/*
@Module
//@InstallIn(ApplicationComponent::class)
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
}*/


@JvmField
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database"
        ).build()
    }
    single  {get<AppDatabase>().dao()}

}
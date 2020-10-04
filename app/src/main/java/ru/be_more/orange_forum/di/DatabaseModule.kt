package ru.be_more.orange_forum.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.be_more.orange_forum.data.local.db.AppDatabase

@JvmField
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single  {get<AppDatabase>().dao()}

}
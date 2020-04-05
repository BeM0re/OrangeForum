package ru.be_more.orange_forum

import android.app.Application
import android.content.Context
import androidx.room.Room

class App : Application() {

    companion object {
//        private var database: AppDatabase? = null
        private var instance: App? = null

        fun applicationContext(): Context = instance!!.applicationContext

        fun getInstance(): App? = instance

//        fun getDatabase(): AppDatabase = database!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        database = Room
//            .databaseBuilder(this, AppDatabase::class.java, DB_NAME)
//            .build()

//        AppCenter.start(
//            getInstance(), APP_SECRET,
//            Analytics::class.java, Crashes::class.java
//        )
    }


}
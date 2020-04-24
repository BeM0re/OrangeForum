package ru.be_more.orange_forum

import android.app.Application
import android.content.Context
import ru.be_more.orange_forum.di.components.DaggerRepoComponent
import ru.be_more.orange_forum.di.components.RepoComponent


open class App : Application() {

    companion object {
//        private var database: AppDatabase? = null
        private var instance: App? = null

        fun applicationContext(): Context = instance!!.applicationContext

        fun getInstance(): App? = instance

        private var component: RepoComponent = DaggerRepoComponent.create()

        fun getComponent(): RepoComponent {
            return component
        }

//        fun getDatabase(): AppDatabase = database!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        createComponent()

//        database = Room
//            .databaseBuilder(this, AppDatabase::class.java, DB_NAME)
//            .build()

//        AppCenter.start(
//            getInstance(), APP_SECRET,
//            Analytics::class.java, Crashes::class.java
//        )
    }

    protected open fun createComponent() {
        component = DaggerRepoComponent.builder()
            .build()
    }


}
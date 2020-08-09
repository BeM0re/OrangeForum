package ru.be_more.orange_forum

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.anadeainc.rxbus.BusProvider
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.be_more.orange_forum.bus.Event
import ru.be_more.orange_forum.data.AppDatabase
import ru.be_more.orange_forum.di.components.DaggerRepoComponent
import ru.be_more.orange_forum.di.components.RepoComponent


open class App : Application() {

    companion object {
//        private var database: AppDatabase? = null
        private var instance: App? = null
        private var database: AppDatabase? = null
        private var component: RepoComponent = DaggerRepoComponent.create()
        private var bus:Subject<Pair<Event, String>> = PublishSubject.create()

        fun getBusInstance() = BusProvider.getInstance()

        fun applicationContext(): Context = instance!!.applicationContext

        fun getInstance(): App? = instance

        fun getDatabase(): AppDatabase = database!!

        fun getComponent(): RepoComponent {
            return component
        }

        fun showToast(message: String) {
            Toast.makeText(this.applicationContext(), message, Toast.LENGTH_LONG).show()
        }

        fun getBus() = this.bus



//        fun getDatabase(): AppDatabase = database!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room
            .databaseBuilder(this, AppDatabase::class.java, "database")
            .build()

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
package ru.be_more.orange_forum

import android.app.Application
import android.content.Context
import android.widget.Toast
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.be_more.orange_forum.bus.Event
import ru.be_more.orange_forum.di.modules.*

class App : Application(){

    companion object {
        private var instance: App? = null
        private var bus:Subject<Pair<Event, String>> = PublishSubject.create()

        fun applicationContext(): Context = instance!!.applicationContext

        fun getInstance(): App? = instance

        fun showToast(message: String) {
            Toast.makeText(this.applicationContext(), message, Toast.LENGTH_LONG).show()
        }

        fun getBus() = this.bus
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(
                appModule,
                presenterModule,
                repositoryModule,
                storageModule,
                databaseModule,
                interactorModule,
                networkModule)
            )
        }



//        AppCenter.start(
//            getInstance(), APP_SECRET,
//            Analytics::class.java, Crashes::class.java
//        )
    }

}
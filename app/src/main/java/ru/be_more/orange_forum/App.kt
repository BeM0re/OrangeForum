package ru.be_more.orange_forum

import android.app.Application
import android.content.Context
import android.widget.Toast
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.be_more.orange_forum.presentation.bus.Event
import ru.be_more.orange_forum.di.*

class App : Application(){

    override fun onCreate() {
        super.onCreate()

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
                networkModule
            ))
        }



//        AppCenter.start(
//            getInstance(), APP_SECRET,
//            Analytics::class.java, Crashes::class.java
//        )
    }

}
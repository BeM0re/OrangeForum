package ru.be_more.orange_forum

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
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
    }

}
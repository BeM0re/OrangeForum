package ru.be_more.orange_forum

import android.app.Application
import dagger.android.DaggerApplication
import ru.be_more.orange_forum.di.AppComponent
import ru.be_more.orange_forum.di.DaggerAppComponent

class App : Application(){

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()
    }

    fun getAppComponent() =
        requireNotNull(appComponent) { "App component wasn't initialized" }

}
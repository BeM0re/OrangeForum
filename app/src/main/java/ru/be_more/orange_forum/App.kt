package ru.be_more.orange_forum

import android.app.Application
import ru.be_more.orange_forum.consts.COOKIE
import ru.be_more.orange_forum.di.AppComponent
import ru.be_more.orange_forum.di.DaggerAppComponent
import ru.be_more.ui.dependencies.UiDepProvider

class App : Application(){

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        UiDepProvider.cookie = COOKIE
    }

    fun getAppComponent() =
        requireNotNull(appComponent) { "App component wasn't initialized" }


}
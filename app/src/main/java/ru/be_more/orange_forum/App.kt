package ru.be_more.orange_forum

import android.content.Context
import android.widget.Toast
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.be_more.orange_forum.bus.Event
import ru.be_more.orange_forum.di.components.DaggerAppComponent


class App : DaggerApplication(){

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


//        AppCenter.start(
//            getInstance(), APP_SECRET,
//            Analytics::class.java, Crashes::class.java
//        )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder()
            .application(this)
            .build()
}
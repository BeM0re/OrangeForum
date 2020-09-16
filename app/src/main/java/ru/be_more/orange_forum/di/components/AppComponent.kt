package ru.be_more.orange_forum.di.components

import android.app.Application
//import dagger.BindsInstance
//import dagger.Component
//import dagger.android.AndroidInjectionModule
//import dagger.android.AndroidInjector
import org.koin.java.KoinJavaComponent.inject
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.di.modules.*
import ru.be_more.orange_forum.ui.main.MainActivity
//import javax.inject.Singleton

/*
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        RepositoryModule::class,
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        StorageModule::class,
        InteractorModule::class,
        RepositoryModule::class
//        PresenterModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    override fun inject(instance: App?)
//    fun inject(instance: MainActivity)
}*/


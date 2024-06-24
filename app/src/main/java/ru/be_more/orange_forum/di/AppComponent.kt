package ru.be_more.orange_forum.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.be_more.orange_forum.presentation.screens.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [
    VmBindModule::class,                    InteractorModule::class,            NetworkModule::class,
    NetworkBindModule::class,               RepoModule::class,                  DatabaseModule::class,
    StorageModule::class,
])
interface AppComponent {
    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}
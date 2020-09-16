package ru.be_more.orange_forum.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.be_more.orange_forum.data.local.storage.StorageContract
import ru.be_more.orange_forum.data.local.storage.StorageContractImpl

//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
//import javax.inject.Singleton

/*
@Module
//@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    @JvmStatic
    fun providePreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
}*/


@JvmField
val appModule = module {
    single { provideSettingsPreferences(androidApplication()) }
}

private fun provideSettingsPreferences(app: Application): SharedPreferences =
    app.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
package ru.be_more.orange_forum.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
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
}
package ru.be_more.orange_forum.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

@JvmField
val appModule = module {
    single { provideSettingsPreferences(androidApplication()) }
}

private fun provideSettingsPreferences(app: Application): SharedPreferences =
    app.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
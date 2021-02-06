package ru.be_more.orange_forum.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.be_more.orange_forum.data.local.prefs.Preferences

@JvmField
val appModule = module {
    single { provideSettingsPreferences(androidApplication()) }
    single { Preferences(get()) }
}

private fun provideSettingsPreferences(app: Application): SharedPreferences =
    app.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
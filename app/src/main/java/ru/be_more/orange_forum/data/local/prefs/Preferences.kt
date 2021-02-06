package ru.be_more.orange_forum.data.local.prefs

import android.content.SharedPreferences

class Preferences (private val sharedPrefs: SharedPreferences){
    var queueToUpdate = true
    var favsToUpdate = true
}
package ru.be_more.orange_forum.extentions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object LifecycleOwnerExtensions {

    fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (T) -> Unit) {
        liveData.observe(this, Observer {
            it?.let { t -> observer(t) }
        })
    }
}
package ru.be_more.orange_forum.presentation.screens.base

import android.app.Activity
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBindingProvider<TViewBinding : ViewBinding>(
    private val clazz: Class<TViewBinding>
) : ReadOnlyProperty<Activity, TViewBinding> {
    private var binding: TViewBinding? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): TViewBinding =
        if (binding != null) binding!!
        else inflateBinding(clazz, thisRef.layoutInflater).also { binding = it }

    @Suppress("UNCHECKED_CAST")
    private fun <TViewBinding> inflateBinding(
        clazz: Class<TViewBinding>,
        layoutInflater: LayoutInflater
    ): TViewBinding =
        clazz.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as TViewBinding
}
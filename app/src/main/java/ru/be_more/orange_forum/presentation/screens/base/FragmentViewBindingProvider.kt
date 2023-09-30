package ru.be_more.orange_forum.presentation.screens.base

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingProvider <TViewBinding : ViewBinding>( //todo delete
    private val clazz: Class<TViewBinding>,
    lifecycleOwner: LifecycleOwner
) : ReadOnlyProperty<Fragment, TViewBinding>, LifecycleObserver {

    private var binding: TViewBinding? = null

    init {
        lifecycleOwner.lifecycle
            .addObserver(this)
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): TViewBinding =
        when {
            thisRef.view == binding?.root && binding != null -> binding!!
            thisRef.view != null -> bindBinding(clazz, thisRef.requireView())
            else -> throw Exception("View is not inflated yet or binding already nullified")
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        binding = null
    }

    @Suppress("UNCHECKED_CAST")
    private fun <TViewBinding> bindBinding(clazz: Class<TViewBinding>, view: View): TViewBinding =
        clazz.getMethod("bind", View::class.java)
            .invoke(null, view) as TViewBinding
}
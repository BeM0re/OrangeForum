package ru.be_more.orange_forum.presentation.screens.base

import android.widget.Toast
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import ru.be_more.orange_forum.App

abstract class BaseFragment<TViewBinding : ViewBinding> : Fragment {

    constructor() : super()

    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected abstract val binding: TViewBinding

    protected inline fun <reified T : ViewBinding> LifecycleOwner.viewBinding() = FragmentViewBindingProvider(T::class.java, this)

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
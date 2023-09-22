package ru.be_more.orange_forum.presentation.screens.base

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<TViewBinding : ViewBinding> : Fragment {

    constructor() : super()

    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected abstract val binding: TViewBinding

    protected inline fun <reified T : ViewBinding> LifecycleOwner.viewBinding() = FragmentViewBindingProvider(T::class.java, this)

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard(){
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**@return true if need to handle back press, false if it was handled*/
    open fun onBackPressed() : Boolean {
        return true
    }
}
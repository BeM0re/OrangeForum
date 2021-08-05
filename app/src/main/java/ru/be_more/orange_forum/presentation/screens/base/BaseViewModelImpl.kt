package ru.be_more.orange_forum.presentation.screens.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.presentation.PresentationContract

abstract class BaseViewModelImpl : ViewModel(), PresentationContract.BaseViewModel {
    private var privateDisposables: CompositeDisposable? = CompositeDisposable()

    protected val disposables
        get() = this.privateDisposables!!

    override val  error = MutableLiveData<String>()

    override fun onDestroy(){
        privateDisposables?.dispose()
        privateDisposables = null
    }

    fun Disposable.addToSubscribe(){
        disposables.add(this)
    }
}
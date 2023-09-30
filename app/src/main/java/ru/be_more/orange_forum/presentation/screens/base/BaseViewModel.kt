package ru.be_more.orange_forum.presentation.screens.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseViewModel : ViewModel() {
    private var privateDisposables: CompositeDisposable? = CompositeDisposable()

    protected val disposables
        get() = this.privateDisposables

    val  error = MutableLiveData<String>()

    open fun onDestroy(){
        privateDisposables?.dispose()
        privateDisposables = null
    }

    fun Disposable.addToSubscribe(){
        disposables?.add(this)
    }

    fun <T> Single<T>.defaultThreads(): Single<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> Observable<T>.defaultThreads(): Observable<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> Flowable<T>.defaultThreads(): Flowable<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> Maybe<T>.defaultThreads(): Maybe<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun Completable.defaultThreads(): Completable {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}
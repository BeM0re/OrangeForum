package ru.be_more.orange_forum.presentation.screens.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private var disposables = CompositeDisposable()

    private val navMutableState = MutableSharedFlow<NavigationState>()
    open val navState = navMutableState.asSharedFlow()

    val error = MutableLiveData<String>()

    open fun onDestroy(){
        disposables.dispose()
        disposables.clear()
    }

    protected fun navigateToBoard(boardId: String) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToBoard(boardId)
            )
        }
    }

    protected fun navigateToThread(boardId: String, threadNum: Int) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToThread(boardId, threadNum)
            )
        }
    }

    protected fun navigateToReply(boardId: String, threadNum: Int, additionalString: String) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToReply(boardId, threadNum, additionalString)
            )
        }
    }

    protected fun navigateToThreadCreating(boardId: String) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToThreadCreating(boardId)
            )
        }
    }

    protected fun Disposable.addToSubscribe(){
        disposables.add(this)
    }

    protected fun <T> Single<T>.defaultThreads(): Single<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    protected fun <T> Observable<T>.defaultThreads(): Observable<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    protected fun <T> Flowable<T>.defaultThreads(): Flowable<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    protected fun <T> Maybe<T>.defaultThreads(): Maybe<T> {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    protected fun Completable.defaultThreads(): Completable {
        return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
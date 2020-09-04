package ru.be_more.orange_forum.extentions

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

val disposables = LinkedList<Disposable>()

fun <T> Single<T>.processSingle(): Single<T> =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { disposable ->
            disposables.add(disposable)
        }
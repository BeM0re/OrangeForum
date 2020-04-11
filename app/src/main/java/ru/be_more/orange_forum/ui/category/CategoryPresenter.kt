package ru.be_more.orange_forum.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.repositories.DvachApiRepository

@InjectViewState
class CategoryPresenter : MvpPresenter<CategoryView>() {

    private var repo = DvachApiRepository
    private var disposable : Disposable? = null

    override fun onFirstViewAttach(){
        disposable?.dispose()
        disposable = repo.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{viewState.loadCategories(it)}
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
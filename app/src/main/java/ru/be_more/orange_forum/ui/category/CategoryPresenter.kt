package ru.be_more.orange_forum.ui.category

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import javax.inject.Inject

@InjectViewState
class CategoryPresenter @Inject constructor(
    private val interactor : InteractorContract.CategoryInteractor
) : MvpPresenter<CategoryView>() {

    private var disposable : Disposable? = null

    override fun onFirstViewAttach(){
        disposable?.dispose()
        disposable = repo.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { viewState.loadCategories(it) },
                { App.showToast("Can't load categories") }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
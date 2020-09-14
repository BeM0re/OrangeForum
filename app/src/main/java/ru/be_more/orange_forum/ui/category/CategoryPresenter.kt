package ru.be_more.orange_forum.ui.category

import android.annotation.SuppressLint
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

    //Здесь и в других презентерах Disposable не сохраняется, т.к. он сохраняется в репо
    @SuppressLint("CheckResult")
    override fun onFirstViewAttach(){
        interactor.getCategories()
            .subscribe(
                { viewState.loadCategories(it) },
                { App.showToast("Can't load categories") }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.release()
    }
}
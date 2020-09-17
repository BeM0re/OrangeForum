package ru.be_more.orange_forum.ui.category

import android.annotation.SuppressLint
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract

class CategoryPresenter(
    private val interactor : InteractorContract.CategoryInteractor,
    private var viewState: CategoryView?
)  {

    //Здесь и в других презентерах Disposable не сохраняется, т.к. он сохраняется в репо
    @SuppressLint("CheckResult")
    fun initPresenter(){
        interactor.getCategories()
            .subscribe(
                { viewState?.loadCategories(it) },
                { App.showToast("Can't load categories") }
            )
    }

    fun onDestroy() {
        viewState = null
        interactor.release()
    }
}
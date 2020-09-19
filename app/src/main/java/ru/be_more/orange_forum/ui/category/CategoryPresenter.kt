package ru.be_more.orange_forum.ui.category

import android.annotation.SuppressLint
import ru.be_more.orange_forum.domain.InteractorContract
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.repositories.DvachApiRepository
import java.util.*
import javax.inject.Inject

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

    fun search(query: String){
        if (query.isEmpty())
            viewState.loadCategories(dataset.value.orEmpty())
        else{
            val filterDataset: LinkedList<Category> = LinkedList()
            for (category in dataset.value!!){
                filterDataset.add(
                    Category(
                        category.title,
                        category.items.filter {
                            (it?.id!!.contains(query, true) ||
                                    it.name.contains(query, true))
                        }
                    )
                )
            }
            viewState.loadCategories(filterDataset.filter { it.items.isNotEmpty() })
            viewState.expandCategories()
        }
    }
}
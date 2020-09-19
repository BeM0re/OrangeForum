package ru.be_more.orange_forum.ui.category

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.model.Category
import java.util.*

class CategoryPresenter(
    private val interactor : InteractorContract.CategoryInteractor,
    private var viewState: CategoryView?
)  {

    private var dataset: MutableLiveData<List<Category>> = MutableLiveData()

    //Здесь и в других презентерах Disposable не сохраняется, т.к. он сохраняется в репо
    @SuppressLint("CheckResult")
    fun initPresenter(){
        dataset.observeForever { data -> viewState?.loadCategories(data) }

        interactor.getCategories()
            .subscribe(
                { dataset.postValue(it) },
                { App.showToast("Can't load categories") }
            )
    }

    fun onDestroy() {
        viewState = null
        interactor.release()
    }

    fun search(query: String){
        if (query.isEmpty())
            viewState?.loadCategories(dataset.value.orEmpty())
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
            viewState?.loadCategories(filterDataset.filter { it.items.isNotEmpty() })
            viewState?.expandCategories()
        }
    }
}
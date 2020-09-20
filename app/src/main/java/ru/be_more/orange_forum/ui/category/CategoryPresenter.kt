package ru.be_more.orange_forum.ui.category

import android.annotation.SuppressLint
import android.util.Log
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
    private var firstLaunch = true
    private var expandedItems: List<Int> = listOf()
    private var savedQuery = ""

    //Здесь и в других презентерах Disposable не сохраняется, т.к. он сохраняется в репо
    @SuppressLint("CheckResult")
    fun initPresenter(view: CategoryView){
        if(firstLaunch){
            dataset.observeForever { data -> viewState?.loadCategories(data) }

            interactor.getCategories()
                .subscribe(
                    {
                        dataset.postValue(it)
                        firstLaunch = false
                    },
                    { App.showToast("Can't load categories") }
                )
        }
        else{
            this.viewState = view
            viewState?.loadCategories(dataset.value!!)
            viewState?.restoreState(expandedItems, savedQuery)
        }
    }

    fun getView() =
        viewState

    fun onDestroy() {
        viewState = null
//        interactor.release()
    }

    fun saveQuery(query: String){
        savedQuery = query
    }

    fun saveExpanded(list: List<Int>){
        expandedItems = list
    }

    fun search(query: String){
        App.showToast(query)
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
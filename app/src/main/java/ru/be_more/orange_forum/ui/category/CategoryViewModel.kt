package ru.be_more.orange_forum.ui.category

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Category
import java.util.*

class CategoryViewModel(
    private val interactor : InteractorContract.CategoryInteractor
): ViewModel() {

    private var fullDataset: List<Category>? = null
    val dataset = MutableLiveData<List<Category>>()
    var expand = MutableLiveData<Boolean>()
    var savedQuery = MutableLiveData<String>()
    private var firstLaunch = true
    private var expandedItems: List<Int> = listOf()
    private var savedQ = ""

    //Здесь и в других презентерах Disposable не сохраняется, т.к. он сохраняется в репо
    @SuppressLint("CheckResult")
    fun initViewModel(){
        if(firstLaunch){
            interactor.getCategories()
                .subscribe(
                    {
                        fullDataset = it
                        dataset.postValue(fullDataset)
                        expand.postValue(false)
                        firstLaunch = false
                    },
                    { App.showToast("Can't load categories") }
                )
        }
        else{
            dataset.postValue(fullDataset)
            expand.postValue(false)
            savedQuery.postValue(savedQ)
        }
    }

    fun saveQuery(query: String){
        this.savedQ = query
    }

    fun saveExpanded(list: List<Int>){
        expandedItems = list
    }

    fun search(query: String){
        if (query.isEmpty()) {
            dataset.postValue(fullDataset)
            expand.postValue(false)
        }
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
            dataset.postValue(filterDataset.filter { it.items.isNotEmpty() })
            expand.postValue(true)
        }
    }

}
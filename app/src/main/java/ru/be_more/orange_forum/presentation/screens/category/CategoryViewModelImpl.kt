package ru.be_more.orange_forum.presentation.screens.category

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.presentation.PresentationContract
import java.util.*

class CategoryViewModelImpl(
    private val interactor : InteractorContract.CategoryInteractor,
    private val prefs: Preferences
): PresentationContract.CategoryViewModel {

    private var fullDataset: List<Category>? = null
    private var firstLaunch = true
    private var expandedItems: List<Int> = listOf()
    private var savedQ = ""
    private var disposables: CompositeDisposable? = CompositeDisposable()

    override val dataset = MutableLiveData<List<Category>>()
    override var expand = MutableLiveData<Boolean>()
    override var savedQuery = MutableLiveData<String>()

    override fun initViewModel(){
        if(firstLaunch){
            disposables?.add(
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
            )
        }
        else{
            dataset.postValue(fullDataset)
            expand.postValue(false)
            savedQuery.postValue(savedQ)
        }
    }

    override fun saveQuery(query: String){
        this.savedQ = query
    }

    override fun saveExpanded(list: List<Int>){
        expandedItems = list
    }

    override fun search(query: String){
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

    override fun onDestroy(){
        disposables?.dispose()
        disposables = null
    }
}
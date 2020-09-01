package ru.be_more.orange_forum.ui.category

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

@InjectViewState
class CategoryPresenter : MvpPresenter<CategoryView>() {

    @Inject
    lateinit var repo : DvachApiRepository //= App.getComponent().getApiRepo()
    private var disposable : Disposable? = null
    private var dataset: MutableLiveData<List<Category>> = MutableLiveData()

    init {
        App.getComponent().inject(this)
        dataset.observeForever { data -> viewState.loadCategories(data) }
    }

    override fun onFirstViewAttach(){
        disposable?.dispose()
        disposable = repo.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dataset.postValue(it) },
                { App.showToast("Can't load categories") }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
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
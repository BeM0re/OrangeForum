package ru.be_more.orange_forum.presentation.screens.category

import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModelImpl
import java.util.*

class CategoryViewModelImpl(
    private val interactor : InteractorContract.CategoryInteractor,
    private val prefs: Preferences
): PresentationContract.CategoryViewModel, BaseViewModelImpl() {

    private var fullDataset: List<Category>? = null
    private var firstLaunch = true
    private var expandedItems = LinkedList<Int>()
    private var savedQ = ""

    override val dataset = MutableLiveData<List<Category>>()
    override var expand = MutableLiveData<List<Int>>()
    override var savedQuery = MutableLiveData<String>()

    override fun initViewModel() {
        if (firstLaunch)
            interactor.getCategories()
                .defaultThreads()
                .subscribe(
                    {
                        fullDataset = it
                        dataset.postValue(fullDataset)
                        expand.postValue(expandedItems)
                        firstLaunch = false
                    },
                    { error.postValue("Can't load categories: \n ${it.message}") }
                )
                .addToSubscribe()
        else {
            dataset.postValue(fullDataset)
            savedQuery.postValue(savedQ)
            expand.postValue(expandedItems)
        }
    }

    override fun search(query: String) {
        savedQ = query
        if (query.isEmpty()) {
            dataset.postValue(fullDataset)
            expandedItems.clear()
            expand.postValue(expandedItems)
        }
        else {
            val filteredDataset = dataset.value
                ?.map { category ->
                    category.copy( boards = category.boards?.filter { board ->
                        board?.id?.contains(query, true) ?: false ||
                                board?.name?.contains(query, true) ?: false
                    })
                }
                ?.filter { !it.boards.isNullOrEmpty() }
            dataset.postValue(filteredDataset)
            expandedItems = LinkedList(filteredDataset?.mapIndexed { index, _ -> index })
            expand.postValue(expandedItems)
        }
    }

    override fun categoryClicked(index: Int) {
        if (!expandedItems.remove(index))
            expandedItems.add(index)
    }
}
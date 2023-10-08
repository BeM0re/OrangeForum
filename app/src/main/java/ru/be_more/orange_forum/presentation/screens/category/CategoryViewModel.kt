package ru.be_more.orange_forum.presentation.screens.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.be_more.orange_forum.data.local.prefs.Preferences
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.presentation.data.ListItemArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class CategoryViewModel(
    private val interactor : InteractorContract.CategoryInteractor,
    private val prefs: Preferences
): BaseViewModel() {

    var items by mutableStateOf(listOf<ListItemArgs>())
        private set

    init {
        interactor
            .observe()
            .map { prepareList(it)}
            .defaultThreads()
            .subscribe(
                { items = it },
                { error.postValue("CategoryViewModel.initViewModel: \n ${it.message}") }
            )
            .addToSubscribe()
    }

    private fun prepareList(categoryList: List<Category>): List<ListItemArgs> =
        mutableListOf<ListItemArgs>().apply {
            categoryList.forEach { category ->
                add(
                    CategoryListItemViewInitArgs(category.name) {
                        setCategoryExpanded(it)
                    }
                )
                if (category.isExpanded)
                    category.boards.forEach { board ->
                        add(
                            BoardShortListItemViewInitArgs(
                                id = board.id,
                                title = board.name,
                            )
                        )
                    }
            }
        }

    private fun setCategoryExpanded(name: String) {
        interactor.toggleExpanded(name)
            .defaultThreads()
            .subscribe(
                {},
                { error.postValue("CategoryViewModel.setCategoryExpanded: \n ${it.message}") }
            )
            .addToSubscribe()
    }

    fun search(query: String) =
        interactor.setSearchQuery(query)
}
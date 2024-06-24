package ru.be_more.orange_forum.presentation.screens.category

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category
import ru.be_more.orange_forum.presentation.composeViews.initArgs.BoardShortListItemViewInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.CategoryListItemViewInitArgs
import ru.be_more.orange_forum.presentation.composeViews.initArgs.ListItemArgs
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    private val interactor: InteractorContract.CategoryInteractor,
) : BaseViewModel() {

    var items = MutableStateFlow(listOf<ListItemArgs>())
        private set

    init {
        showLoading()

        viewModelScope.launch(CoroutineExceptionHandler{_, _ -> }){}

        runCoroutine("init.getListFlow") {
            interactor
                .getCategoryListFlow()
                .map { prepareList(it)}
                .flowOn(Dispatchers.IO)
                .collect { items.emit(it) }
        }

        runCoroutine("init.refresh") {
            interactor.refresh()
            showContent()
        }
    }

    private fun prepareList(categoryList: List<Category>): List<ListItemArgs> =
        mutableListOf<ListItemArgs>().apply {
            categoryList.forEach { category ->
                add(
                    CategoryListItemViewInitArgs(
                        title = category.name,
                        onClick = ::setCategoryExpanded
                    )
                )
                if (category.isExpanded)
                    category.boards.forEach { board ->
                        add(
                            BoardShortListItemViewInitArgs(
                                id = board.id,
                                title = board.name,
                                onClick = ::navigateToBoard
                            )
                        )
                    }
            }
        }

    private fun setCategoryExpanded(name: String) =
        runCoroutine("init.refresh") {
            interactor.toggleExpanded(name)
        }

    fun search(query: String) =
        viewModelScope.launch {
            interactor.search(query)
        }
}
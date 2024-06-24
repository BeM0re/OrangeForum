package ru.be_more.orange_forum.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category
import javax.inject.Inject

class CategoryInteractorImpl @Inject constructor(
    private val apiRepository: RemoteContract.ApiRepository,
    private val categoryRepository: DbContract.CategoryRepository,
    private val boardRepository: DbContract.BoardRepository,
): InteractorContract.CategoryInteractor {

    private val searchQuery = MutableStateFlow("")

    override fun getCategoryListFlow(): Flow<List<Category>> =
        combine(
            searchQuery,
            getCategoryFlow()
        ) { query, categories ->
            if (query.isEmpty()) categories
            else categories
                .map { category ->
                    category.copy(
                        boards = category.boards
                            .filter { it.name.contains(query) || it.id.contains(query) }
                    )
                }
                .filter { it.boards.isNotEmpty() }
        }

    override suspend fun refresh() =
        apiRepository.getCategories()
            .let { categories ->
                categoryRepository.delete()
                categoryRepository.insert(categories)
                boardRepository.insertKeepingState(
                    categories
                        .map { it.boards }
                        .flatten()
                )
            }

    override suspend fun toggleExpanded(name: String) =
        categoryRepository.getEmpty(name)
            .let { category ->
                categoryRepository.setIsExpanded(name, !category.isExpanded)
            }

    override suspend fun search(query: String) =
        searchQuery.emit(query)

    private fun getCategoryFlow(): Flow<List<Category>> =
        combine(
            categoryRepository.getFlowList(),
            boardRepository.getListFlow()
        ) { categoryList, boardList ->
            val boardMap = boardList.groupBy { it.category }
            categoryList.map { category ->
                category.copy(boards = boardMap.getOrDefault(category.name, listOf()))
            }
        }
}
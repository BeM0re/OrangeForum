package ru.be_more.orange_forum.domain.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.model.model.Category
import ru.be_more.model.model.ImageboardType
import javax.inject.Inject
import kotlin.reflect.KClass

class CategoryInteractorImpl @Inject constructor(
    private val categoryRepository: DbContract.CategoryRepository,
    private val boardRepository: DbContract.BoardRepository,
    private val apiRepositoryMap: Map<ImageboardType, @JvmSuppressWildcards RemoteContract.ApiRepository>,
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
        withContext(Dispatchers.IO) {
            categoryRepository.delete()
            apiRepositoryMap.forEach { (_, api) ->
                launch {
                    api.getCategories()
                        .let { categories ->
                            categoryRepository.insert(categories)
                            boardRepository.insertKeepingState(
                                categories
                                    .map { it.boards }
                                    .flatten()
                            )
                        }
                }
            }

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
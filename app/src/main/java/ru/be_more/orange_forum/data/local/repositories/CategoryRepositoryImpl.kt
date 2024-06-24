package ru.be_more.orange_forum.data.local.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.be_more.orange_forum.data.local.db.dao.CategoryDao
import ru.be_more.orange_forum.data.local.db.entities.StoredCategory
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.model.Category
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
) : DbContract.CategoryRepository {

    override suspend fun insert(categories: List<Category>) =
        dao.insert(
            categories.map { StoredCategory(it) }
        )

    override fun getFlowList(): Flow<List<Category>> =
        dao.getCategoryListFlow()
            .map { list ->
                list.map { it.toModel() }
            }

    override suspend fun getEmpty(name: String): Category =
        dao.getCategory(name)
            .toModel()

    override suspend fun setIsExpanded(name: String, isExpanded: Boolean) =
        dao.setIsExpanded(name, isExpanded)

    override suspend fun delete() =
        dao.delete()
}
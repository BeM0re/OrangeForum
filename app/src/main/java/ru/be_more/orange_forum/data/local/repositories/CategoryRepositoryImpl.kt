package ru.be_more.orange_forum.data.local.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.be_more.database.db.dao.CategoryDao
import ru.be_more.model.model.Category
import ru.be_more.orange_forum.data.local.dbConverters.CategoryFactory
import ru.be_more.orange_forum.domain.contracts.DbContract
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
) : DbContract.CategoryRepository {

    override suspend fun insert(categories: List<Category>) =
        dao.insert(
            categories.map { CategoryFactory.toEntity(it) }
        )

    override fun getFlowList(): Flow<List<Category>> =
        dao.getCategoryListFlow()
            .map { list ->
                list.map { CategoryFactory.fromEntity(it) }
            }

    override suspend fun getEmpty(name: String): Category =
        dao.getCategory(name)
            .let { CategoryFactory.fromEntity(it) }

    override suspend fun setIsExpanded(name: String, isExpanded: Boolean) =
        dao.setIsExpanded(name, isExpanded)

    override suspend fun delete() =
        dao.delete()
}
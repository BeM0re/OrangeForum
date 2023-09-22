package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.dao.CategoryDao
import ru.be_more.orange_forum.data.local.db.entities.StoredCategory
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.model.Category

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : DbContract.CategoryRepository {
    override fun insert(categories: List<Category>): Completable =
        dao.insert(
            categories.map { StoredCategory(it) }
        )

    override fun observe(): Observable<List<Category>> =
        dao.observeCategories()
            .map {  list ->
                list.map { it.toModel() }
            }

    override fun get(name: String): Single<Category> =
        dao.getCategory(name)
            .map { it.toModel() }

    override fun setIsExpanded(name: String, isExpanded: Boolean): Completable =
        dao.setIsExpanded(name, isExpanded)
}
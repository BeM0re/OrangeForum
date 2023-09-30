package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.dao.BoardDao
import ru.be_more.orange_forum.data.local.db.dao.CategoryDao
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredCategory
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.model.Category

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val boardDao: BoardDao
) : DbContract.CategoryRepository {
    override fun insert(categories: List<Category>): Completable =
        categoryDao
            .insert(
                categories.map { StoredCategory(it) }
            )
            .andThen(
                boardDao.insertBoardList(
                    categories
                        .map { it.boards }
                        .flatten()
                        .map {StoredBoard(it) }
                )
            )

    override fun observe(): Observable<List<Category>> =
        Observable.combineLatest(
            categoryDao
                .observeCategories()
                .map {  list ->
                    list.map { it.toModel() }
                },
            boardDao
                .observeList()
                .map {  list ->
                    list.map { it.toModel() }
                }
        ) { categoryList, boardList ->
            val boardMap = boardList.groupBy { it.category }
            categoryList.map { category ->
                category.copy(boards = boardMap.getOrDefault(category.name, listOf()))
            }
        }


    override fun getEmpty(name: String): Single<Category> =
        categoryDao.getCategory(name)
            .map { it.toModel() }

    override fun setIsExpanded(name: String, isExpanded: Boolean): Completable =
        categoryDao.setIsExpanded(name, isExpanded)
}
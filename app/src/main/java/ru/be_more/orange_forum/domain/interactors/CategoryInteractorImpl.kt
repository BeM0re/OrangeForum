package ru.be_more.orange_forum.domain.interactors

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category

class CategoryInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val categoryRepository: DbContract.CategoryRepository,
    private val boardRepository: DbContract.BoardRepository,
): InteractorContract.CategoryInteractor {

    private val searchQuery = BehaviorSubject
        .createDefault("")

    override fun observe(): Observable<List<Category>> =
        apiRepository.getCategories()
            .flatMapCompletable { categories ->
                categoryRepository.insert(categories)
                    .andThen(
                        boardRepository.insert(
                            categories
                                .map { it.boards }
                                .flatten()
                        )
                    )

            }
            .andThen(
                Observable.combineLatest(
                    searchQuery.toFlowable(BackpressureStrategy.LATEST).toObservable(),
                    observeCategories()
                ) { query, categories ->
                    if (query.isEmpty()) categories
                    else categories
//                        .filter { category ->
//                        category.name.contains(query)
//                                || category.boards.any { it.name.contains(query) }
//                    }
                        .map { category ->
                            category.copy(
                                boards = category.boards
                                    .filter { it.name.contains(query) || it.id.contains(query) }
                            )
                        }
                        .filter { it.boards.isNotEmpty() }
                }

            )

    override fun toggleExpanded(name: String): Completable =
        categoryRepository.getEmpty(name)
            .flatMapCompletable {
                categoryRepository.setIsExpanded(name, !it.isExpanded)
            }

    override fun setSearchQuery(query: String) =
        searchQuery.onNext(query)

    private fun observeCategories(): Observable<List<Category>> =
        Observable.combineLatest(
            categoryRepository.observe(),
            boardRepository.observeList()
        ) { categoryList, boardList ->
            val boardMap = boardList.groupBy { it.category }
            categoryList.map { category ->
                category.copy(boards = boardMap.getOrDefault(category.name, listOf()))
            }
        }
}
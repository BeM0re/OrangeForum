package ru.be_more.orange_forum.domain.interactors

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category

class CategoryInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val dbRepository: DbContract.CategoryRepository,
): InteractorContract.CategoryInteractor {

    private val searchQuery = BehaviorSubject
        .createDefault("")

    override fun get(): Observable<List<Category>> =
        apiRepository.getCategories()
            .flatMapCompletable {
                dbRepository.insert(it)
            }
            .andThen(
                Observable.combineLatest(
                    searchQuery.toFlowable(BackpressureStrategy.LATEST).toObservable(),
                    dbRepository.observe()
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

    override fun setIsExpanded(name: String): Completable =
        dbRepository.getEmpty(name)
            .flatMapCompletable {
                dbRepository.setIsExpanded(name, !it.isExpanded)
            }

    override fun setSearchQuery(query: String) =
        searchQuery.onNext(query)
}
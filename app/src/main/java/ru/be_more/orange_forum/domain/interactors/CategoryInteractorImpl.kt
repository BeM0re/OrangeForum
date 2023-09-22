package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Observable
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category

class CategoryInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val dbRepository: DbContract.CategoryRepository,
): InteractorContract.CategoryInteractor {

    override fun get(): Observable<List<Category>> =
        apiRepository.getCategories()
            .flatMapCompletable {
                dbRepository.insert(it)
            }
            .andThen(
                dbRepository.observe()
            )

    override fun setIsExpanded(name: String, isExpanded: Boolean): Completable =
        dbRepository.get(name)
            .flatMapCompletable {
                dbRepository.setIsExpanded(name, !it.isExpanded)
            }
}
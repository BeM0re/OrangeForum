package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Category

class CategoryInteractorImpl (
    private val repository: RemoteContract.ApiRepository):
        InteractorContract.CategoryInteractor {

    override fun getCategories(): Single<List<Category>> =
        repository.getDvachCategories()
}
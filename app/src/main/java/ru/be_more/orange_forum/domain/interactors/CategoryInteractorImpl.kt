package ru.be_more.orange_forum.domain.interactors

import android.annotation.SuppressLint
import io.reactivex.Single
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.converters.RemoteConverter
import ru.be_more.orange_forum.domain.model.Category
import javax.inject.Inject

class CategoryInteractorImpl @Inject constructor(
    private val repository: RemoteContract.CategoryRepository):
        InteractorContract.CategoryInteractor, BaseInteractorImpl() {

    @SuppressLint("CheckResult")
    override fun getCategories(): Single<List<Category>> =
        repository.getDvachCategories().map { RemoteConverter.toCategories(it) }
}
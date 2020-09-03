package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.models.DvachBoardName
import ru.be_more.orange_forum.data.remote.models.RemoteConverter.Companion.toCategories
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.model.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val dvachApi : DvachApi
) : RemoteContract.CategoryRepository{

    override fun getDvachCategories(task: String): Observable<List<Category>> =
        dvachApi.getDvachCategories("get_boards")
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.d("M_DvachApiRepository", "Getting category error = $throwable") }
            .map { entity -> toCategories(entity) }
}
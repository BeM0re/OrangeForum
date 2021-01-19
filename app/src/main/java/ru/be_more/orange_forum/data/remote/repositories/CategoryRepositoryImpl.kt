package ru.be_more.orange_forum.data.remote.repositories

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.models.BoardNameDto

class CategoryRepositoryImpl(
    private val dvachApi : DvachApi
) : RemoteContract.CategoryRepository{

    override fun getDvachCategories(): Single<Map<String, List<BoardNameDto>>> =
        dvachApi.getDvachCategories("get_boards")
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e("M_DvachApiRepository", "Getting category error = $throwable") }
}
package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.data.db.DbContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.processSingle
import javax.inject.Inject

class FavoriteInteractorImpl @Inject constructor(
    private val favoriteRepository: DbContract.FavoriteRepository
): InteractorContract.FavoriteInteractor {

    override fun getFavorites(): Single<List<Board>> =
        favoriteRepository.getFavorites()
            .processSingle()

}
package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.processSingle

class FavoriteInteractorImpl(
    private val favoriteRepository: DbContract.FavoriteRepository
): InteractorContract.FavoriteInteractor, BaseInteractorImpl() {

    override fun getFavorites(): Single<List<Board>> =
        favoriteRepository.getFavorites()
            .processSingle()

}
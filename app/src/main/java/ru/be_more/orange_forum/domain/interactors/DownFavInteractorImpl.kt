package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.processSingle

class DownFavInteractorImpl(
    private val dbDownFavRepository: DbContract.DownFavRepository
): InteractorContract.DownFavInteractor, BaseInteractorImpl() {
    override fun getDownloads(): Single<List<Board>> =
        dbDownFavRepository.getDownloadsAndFavorites()
            .processSingle()
}
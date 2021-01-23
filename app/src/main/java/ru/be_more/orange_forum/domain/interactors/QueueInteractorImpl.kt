package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.processSingle

class QueueInteractorImpl(
    private val favoriteRepository: DbContract.QueueRepository
): InteractorContract.QueueInteractor, BaseInteractorImpl() {

    override fun getQueue(): Single<List<Board>> =
        favoriteRepository.getQueue()
            .processSingle()

}
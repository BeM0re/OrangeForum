package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.InteractorContract

class PostInteractorImpl: InteractorContract.PostInteractor{

    override fun getPost(boardId: String, postNum: Int): Single<Int> =
        Single.just(1)
}
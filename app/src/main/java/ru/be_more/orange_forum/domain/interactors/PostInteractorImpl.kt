package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.InteractorContract

class PostInteractorImpl: InteractorContract.PostInteractor{

    @Deprecated("Maybe delete", ReplaceWith("Single.just(1)", "io.reactivex.Single"))
    override fun getPost(boardId: String, postNum: Int): Single<Int> =
        Single.just(1)
}
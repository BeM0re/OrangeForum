package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.model.Post

class PostInteractorImpl(
    private val dbRepository: DbContract.PostRepository,
    private val apiRepository: RemoteContract.ApiRepository
): InteractorContract.PostInteractor{

    override fun getPost(boardId: String, threadNum: Int, postNum: Int): Single<Post> =
        dbRepository.get(boardId, postNum)
            .switchIfEmpty(
                apiRepository
                    .getPost(boardId, threadNum, postNum)
                    .flatMap { post ->
                        dbRepository
                            .insert(post)
                            .andThen(Single.just(post))
                    }
            )
}
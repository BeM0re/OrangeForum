package ru.be_more.orange_forum.domain.interactors

import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.model.model.Post
import javax.inject.Inject

class PostInteractorImpl @Inject constructor(
    private val dbRepository: DbContract.PostRepository,
    private val apiRepository: RemoteContract.ApiRepository
): InteractorContract.PostInteractor{

    override suspend fun getPost(boardId: String, threadNum: Int, postNum: Int): Post =
        dbRepository.get(boardId, postNum)
            ?: apiRepository
                .getPost(boardId, threadNum, postNum)
                .also { post -> dbRepository.insert(post) }
}
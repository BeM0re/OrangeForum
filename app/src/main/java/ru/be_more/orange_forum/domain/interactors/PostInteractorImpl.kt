package ru.be_more.orange_forum.domain.interactors

import ru.be_more.model.model.ImageboardType
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.model.model.Post
import javax.inject.Inject

class PostInteractorImpl @Inject constructor(
    private val dbRepository: DbContract.PostRepository,
    private val apiRepositoryMap: Map<ImageboardType, @JvmSuppressWildcards RemoteContract.ApiRepository>
): InteractorContract.PostInteractor{

    override suspend fun getPost(
        imageboardType: ImageboardType,
        boardId: String,
        threadNum: Int,
        postNum: Int
    ): Post =
        dbRepository.get(boardId, postNum)
            ?: apiRepositoryMap[imageboardType]
                ?.getPost(boardId, threadNum, postNum)
                ?.also { post -> dbRepository.insert(post) }
            ?: throw IllegalArgumentException("Repository not found for getting post")
}
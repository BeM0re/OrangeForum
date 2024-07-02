package ru.be_more.orange_forum.domain.interactors

import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import javax.inject.Inject
import kotlin.reflect.KClass

class ReplyInteractorImpl @Inject constructor(
    private val dbRepository: DbContract.PostRepository,
    private val apiRepositoryMap: Map<ImageboardType, @JvmSuppressWildcards RemoteContract.ApiRepository>
): InteractorContract.ReplyInteractor {

    override suspend fun getCaptcha(
        imageboardType: ImageboardType,
        boardId: String,
        threadNum: Int?
    ): String =
        apiRepositoryMap[imageboardType]?.getCaptchaUrl(boardId, threadNum)
            ?: throw IllegalStateException("No proper repository found")

    override suspend fun reply(
        imageboardType: ImageboardType,
        boardId: String,
        threadNum: Int,
        comment: String,
        isOp: Boolean,
        subject: String,
        email: String,
        name: String,
        tag: String,
        captchaSolvedString: String?
    ) =
        apiRepositoryMap[imageboardType]
            ?.postReply(
                boardId = boardId,
                threadNum = threadNum,
                comment = comment,
                isOp = isOp,
                subject = subject,
                email = email,
                name = name,
                tag = tag,
                captchaSolvedString = captchaSolvedString,
            )
            ?.let { postNum ->
                apiRepositoryMap[imageboardType]?.getPost(boardId, threadNum, postNum)
            }
            ?.let { post ->
                dbRepository.insert(post.copy(isMyPost = true))
            } ?: throw IllegalStateException("No proper repository found")

    override suspend fun createThread(
        imageboardType: ImageboardType,
        boardId: String,
        comment: String,
        isOp: Boolean,
        subject: String,
        email: String,
        name: String,
        tag: String,
        captchaSolvedString: String?
    ) {
        TODO("Not yet implemented")
    }

}
package ru.be_more.orange_forum.domain.interactors

import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract

class ReplyInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val dbRepository: DbContract.PostRepository,
): InteractorContract.ReplyInteractor {

    override suspend fun getCaptcha(boardId: String, threadNum: Int?): String =
        apiRepository.getCaptchaUrl(boardId, threadNum)

    override suspend fun reply(
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
        apiRepository
            .postReply(
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
            .let { postNum ->
                apiRepository.getPost(boardId, threadNum, postNum)
            }
            .let { post ->
                dbRepository.insert(post.copy(isMyPost = true))
            }

    override suspend fun createThread(
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
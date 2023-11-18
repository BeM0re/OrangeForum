package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.PostResponse

class ReplyInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository,
    private val dbRepository: DbContract.PostRepository,
): InteractorContract.ReplyInteractor {

    override fun getCaptcha(boardId: String, threadNum: Int?): Single<String> =
        apiRepository.getCaptchaUrl(boardId, threadNum)

    override fun reply(
        boardId: String,
        threadNum: Int,
        comment: String,
        isOp: Boolean,
        subject: String,
        email: String,
        name: String,
        tag: String,
        captchaSolvedString: String?,
    ): Completable =
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
            .flatMap { postNum ->
                apiRepository.getPost(boardId, threadNum, postNum)
            }
            .flatMapCompletable { post ->
                dbRepository.insert(
                    post.copy(isMyPost = true)
                )
            }

    override fun createThread(
        boardId: String,
        comment: String,
        isOp: Boolean,
        subject: String,
        email: String,
        name: String,
        tag: String,
        captchaSolvedString: String?
    ): Completable {
        TODO("Not yet implemented")
    }

}
package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.models.DvachPostResponse
import ru.be_more.orange_forum.domain.InteractorContract
//import javax.inject.Inject

class ResponseInteractorImpl /*@Inject constructor*/(
    private val responseRepository: RemoteContract.ResponseRepository
): InteractorContract.ResponseInteractor, BaseInteractorImpl() {
    override fun postResponse(
        boardId: String,
        threadNum: Int,
        comment: String,
        token: String
    ): Single<DvachPostResponse> =
        responseRepository.postResponse(
            boardId = boardId,
            threadNum = threadNum,
            comment = comment,
            captcha_type = "recaptcha",
            g_recaptcha_response = token,
            chaptcha_id = "6LeQYz4UAAAAAL8JCk35wHSv6cuEV5PyLhI6IxsM",
            files = listOf()
        )
}
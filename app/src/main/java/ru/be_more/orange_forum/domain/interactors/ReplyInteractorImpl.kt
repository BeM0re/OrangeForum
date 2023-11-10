package ru.be_more.orange_forum.domain.interactors

import android.util.Log
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.PostResponse

class ReplyInteractorImpl(
    private val apiRepository: RemoteContract.ApiRepository
): InteractorContract.ReplyInteractor {

    override fun getCapture(boardId: String, threadNum: Int?): Single<String> =
        apiRepository.getCaptchaUrl(boardId, threadNum)

    //todo review
    override fun reply(
        boardId: String,
        threadNum: Int,
        comment: String,
        token: String
    ): Single<PostResponse> =
        apiRepository.postResponse(
            boardId = boardId,
            threadNum = threadNum,
            comment = comment,
            captcha_type = "recaptcha",
            g_recaptcha_response = token,
            chaptcha_id = "6LeQYz4UAAAAAL8JCk35wHSv6cuEV5PyLhI6IxsM",
            files = listOf()
        )
            .doOnSubscribe { Log.d("M_ResponseInteractorImpl","pot sub") }
            .doOnSuccess { Log.d("M_ResponseInteractorImpl","post success") }
}
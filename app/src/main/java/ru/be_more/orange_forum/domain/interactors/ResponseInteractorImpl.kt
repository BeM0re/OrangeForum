package ru.be_more.orange_forum.domain.interactors

import android.util.Log
import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.PostResponse

class ResponseInteractorImpl(
    private val responseRepository: RemoteContract.ApiRepository
): InteractorContract.ResponseInteractor {
    //todo review
    override fun postResponse(
        boardId: String,
        threadNum: Int,
        comment: String,
        token: String
    ): Single<PostResponse> =
        responseRepository.postResponse(
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
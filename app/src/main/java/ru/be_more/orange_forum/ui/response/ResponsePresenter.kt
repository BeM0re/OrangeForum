package ru.be_more.orange_forum.ui.response

import android.annotation.SuppressLint
import android.util.Log
import moxy.MvpPresenter
import ru.be_more.orange_forum.domain.InteractorContract
import javax.inject.Inject

class ResponsePresenter @Inject constructor(
    private val interactor : InteractorContract.ResponseInteractor
): MvpPresenter<ResponseView>() {

    @SuppressLint("CheckResult")
    fun postResponse(boardId: String, threadNum: Int, comment: String, token:String){

        interactor.postResponse(
            boardId = boardId,
            threadNum = threadNum,
            comment = comment,
            token = token
        )
            .subscribe (
                { response -> Log.d("M_ResponsePresenter", "post response = $response") },
                { throwable ->  Log.d("M_ResponsePresenter", "post error = $throwable") }
            )
    }

}
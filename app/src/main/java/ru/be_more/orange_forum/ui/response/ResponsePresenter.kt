package ru.be_more.orange_forum.ui.response

import android.annotation.SuppressLint
import android.util.Log
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.App

class ResponsePresenter (
    private val interactor : InteractorContract.ResponseInteractor,
    private var viewState: ResponseView?
){

    @SuppressLint("CheckResult")
    fun postResponse(boardId: String, threadNum: Int, comment: String, token:String){
        interactor.postResponse(
            boardId = boardId,
            threadNum = threadNum,
            comment = comment,
            token = token
        )
            .subscribe (
				{ response ->
					Log.d("M_ResponsePresenter", "post response = $response")
					if(response.Num != 0) //0 - ошибка постинга
						viewState?.closeResponse()
					else
						App.showToast(response.Reason)

				},
				{ throwable ->
					Log.d("M_ResponsePresenter", "post error = $throwable")
				}
            )
    }

    fun onDestroy(){
        interactor.release()
        viewState = null
    }
}
package ru.be_more.orange_forum.presentation.screens.response

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.presentation.PresentationContract

class ResponseViewModelImpl (
    private val interactor : InteractorContract.ResponseInteractor
): PresentationContract.ResponseViewModel{

    override val result = MutableLiveData<String>()

    @SuppressLint("CheckResult")
    override fun postResponse(boardId: String, threadNum: Int, comment: String, token:String){
        interactor.postResponse(
            boardId = boardId,
            threadNum = threadNum,
            comment = comment,
            token = token
        )
            .subscribe (
				{ response ->
					if(response.Num != 0) //0 - ошибка постинга
						result.postValue("")
					else
                        result.postValue(response.Reason)
				},
				{ throwable ->
					Log.e("M_ResponsePresenter", "post error = $throwable")
				}
            )
    }

    override fun onDestroy(){

    }
}
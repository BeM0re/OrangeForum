package ru.be_more.orange_forum.presentation.screens.response

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class ResponseViewModel (
    private val interactor : InteractorContract.ResponseInteractor
): BaseViewModel(){

    val result = MutableLiveData<String>()

    fun postResponse(boardId: String, threadNum: Int, comment: String, token:String){
        interactor.postResponse(
            boardId = boardId,
            threadNum = threadNum,
            comment = comment,
            token = token
        )
            .subscribe (
                { response ->
                    if(response.num != 0) //0 - ошибка постинга
                        result.postValue("")
                    else
                        result.postValue(response.reason)
                },
                { throwable ->
                    Log.e("M_ResponseViewModelImpl", "post error = $throwable")
                }
            )
            .addToSubscribe()
    }

}
package ru.be_more.orange_forum.ui.response

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.consts.THREAD_TAG
import ru.be_more.orange_forum.repositories.DvachApiRepository
import javax.inject.Inject

class ResponsePresenter: MvpPresenter<ResponseView>() {

    @Inject
    lateinit var repo : DvachApiRepository

    private var disposable: Disposable? = null

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    init {
        App.getComponent().inject(this)
    }

    fun postResponse(boardId: String, threadNum: Int, comment: String, token:String){

        disposable?.dispose()

        disposable = //TODO после API доделать (убрать зашитые данные, брать из полей вью)
            repo.postResponse(
                boardId = boardId,
                threadNum = threadNum,
                comment = comment,
                captcha_type = "recaptcha",
                g_recaptcha_response = token,
                chaptcha_id = "6LeQYz4UAAAAAL8JCk35wHSv6cuEV5PyLhI6IxsM",
                files = listOf()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { response ->
                        Log.d("M_ResponsePresenter", "post response = $response")
                        if(response.Num != 0) //0 - ошибка постинга
                            viewState.closeResponse()
                        else
                            App.showToast(response.Reason)

                    },
                    { throwable ->
                        Log.d("M_ResponsePresenter", "post error = $throwable")
                    }
                )
    }

}
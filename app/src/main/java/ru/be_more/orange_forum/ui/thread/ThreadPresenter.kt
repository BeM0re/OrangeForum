package ru.be_more.orange_forum.ui.thread

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository
import java.util.*

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private var isResponseOpen : MutableLiveData<Boolean> = MutableLiveData()

    private var repo = DvachApiRepository
    var thread :BoardThread = BoardThread(num = 0)
    private lateinit var boardId :String
    private var threadNum :Int = 0
    private var disposables : LinkedList<Disposable?> = LinkedList()
    private var isInvisibleRecaptcha: Boolean = false
    private var captchaId: String = ""

    fun openResponseForm(){
        isResponseOpen.postValue(true)
    }

    fun getIsResponseOpen(): LiveData<Boolean> = isResponseOpen

    fun updateThreadData(){
        viewState.loadThread(thread)
    }

    fun init(boardId: String, threadNum: Int){
        this.boardId = boardId
        this.threadNum = threadNum

        disposables.add(
            repo.getThread(boardId, threadNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    thread = it
                    viewState.loadThread(thread)
                }
        )
        getIsResponseOpen().observeForever { viewState.hideResponseFab() }

    }

    fun post(){
        isInvisibleRecaptcha = false
        disposables.add(
            repo.getCaptchaTypes()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe (
                    {
//                        Log.d("M_ThreadPresenter", "C types = $it")
                        if(it.id == "invisible_recaptcha") {

                            repo.getMobileCaptcha().subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe (
                                    {
//                                        Log.d("M_ThreadPresenter", "API response = ${it.string()}")
                                        viewState.setWebView(it.string())
                                    },
                                    {Log.d("M_ThreadPresenter", "error = $it")}
                                )



                            isInvisibleRecaptcha = true
                            disposables.add(repo.getCaptchaId("invisible_recaptcha")
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                    { response -> //response is Captcha
                                        Log.d("M_ThreadPresenter", "$response")
                                        postResponse(response.id, "invisible_recaptcha")
                                    },
                                    { throwable ->
                                        Log.d("M_ThreadPresenter", "getCaptchaId error = $throwable")
                                    }
                                )
                            )
                        }
                    },
                    { Log.d("M_ThreadPresenter", "getCaptchaTypes error = $it") },
                    {
                        if (!isInvisibleRecaptcha)
                            Toast.makeText(
                                App.applicationContext(),
                                App.applicationContext().getString(
                                    R.string.no_invisible_recaptcha_error),
                                Toast.LENGTH_SHORT).show()
                    }
                )
        )


    }

    private fun postResponse(captchaId:String, captchaType:String){

//        Log.d("M_ThreadPresenter", "board = $boardId")
//        Log.d("M_ThreadPresenter", "thr = $threadNum")
//        Log.d("M_ThreadPresenter", "captchaId = $captchaId")




        disposables.add(
            repo.postResponse(
                boardId = boardId,
                threadNum = threadNum ,
                comment = "test",
                captcha_type = captchaType,
                g_recaptcha_response = "",
                chaptcha_id = captchaId,
                files = listOf()
            )
                .subscribeOn(Schedulers.io())
                .subscribe (
                    { response -> Log.d("M_ThreadPresenter", "post response = $response") },
                    { throwable ->  Log.d("M_ThreadPresenter", "post error = $throwable") }
                )
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach {
            it?.dispose()
        }
    }

}
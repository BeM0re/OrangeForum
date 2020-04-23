package ru.be_more.orange_forum.ui.thread

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
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
import ru.be_more.orange_forum.ui.post.PostOnClickListener
import java.util.*

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private lateinit var adapter : ThreadAdapter

    private var repo = DvachApiRepository
    lateinit var thread: BoardThread
    private lateinit var boardId :String
    private var threadNum: Int = 0
    private var disposables: LinkedList<Disposable?> = LinkedList()
    private var isInvisibleRecaptcha: Boolean = false
    private var captchaResponse: MutableLiveData<String> = MutableLiveData()

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
    //TODO прятать fab при нажатии на ответ

        captchaResponse.observeForever {
            Log.d("M_ThreadPresenter", "posting. token = $it")
        }
    }

    fun post(){
        isInvisibleRecaptcha = false
        disposables.add(
            repo.getCaptchaTypes()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe (
                    {
                        if(it.id == "invisible_recaptcha") { //TODO переделать на нативную капчу после API

//                            viewState.setWebView()

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

        disposables.add( //TODO после API доделать (убрать зашитые данные, брать из полей вью)
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

    fun showFooter() {
        adapter.setIsFooterShown(true)
//        viewState.setWebView()
        viewState.setOnPostClickListener()
    }

    fun initAdapter(thread: BoardThread, listener: PostOnClickListener) {
        adapter = ThreadAdapter(thread, listener)
    }

    fun getAdapter(): ThreadAdapter = this.adapter

    fun setCaptchaResponse(captchaResponse: String) {
        Log.d("M_ThreadPresenter", "presenter token = $captchaResponse")
        this.captchaResponse.postValue(captchaResponse)
    }

}
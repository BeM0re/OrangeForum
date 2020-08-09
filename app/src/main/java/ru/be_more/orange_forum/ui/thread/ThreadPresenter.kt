package ru.be_more.orange_forum.ui.thread

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.anadeainc.rxbus.BusProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.DownloadedThreadEntered
import ru.be_more.orange_forum.bus.FavoriteThreadEntered
import ru.be_more.orange_forum.bus.UndownloadedThreadEntered
import ru.be_more.orange_forum.bus.UnfavoriteThreadEntered
import ru.be_more.orange_forum.interactors.ThreadInteractor
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.ModalContent
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import java.util.*
import javax.inject.Inject

@InjectViewState
class ThreadPresenter : MvpPresenter<ThreadView>() {

    private lateinit var adapter : ThreadAdapter

    @Inject
    lateinit var threadInteractor: ThreadInteractor

    @Inject
    lateinit var repo : DvachApiRepository
    lateinit var thread: BoardThread
    private lateinit var boardId :String
    private var threadNum: Int = 0
    private var disposables: LinkedList<Disposable?> = LinkedList()
    private var isInvisibleRecaptcha: Boolean = false
    private var captchaResponse: MutableLiveData<String> = MutableLiveData()

    private val modalStack: Stack<ModalContent> = Stack()

    fun init(boardId: String, threadNum: Int){
        App.getComponent().inject(this)


        this.boardId = boardId
        this.threadNum = threadNum

        disposables.add(
            threadInteractor.getThread(boardId, threadNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        thread = it
                        viewState.loadThread(thread)
                        viewState.setThreadMarks(thread)
                    },
                    {
                        Log.d("M_ThreadPresenter", "get tread in tread presenter error = $it")
                    }
                )
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
        disposables.forEach {
            it?.dispose()
        }
        threadInteractor.destroy()
        super.onDestroy()
    }

    fun showFooter() {
        adapter.setIsFooterShown(true)
//        viewState.setWebView()
        viewState.setOnPostButtonClickListener()
    }

    fun initAdapter(thread: BoardThread,
                    picListener: PicOnClickListener,
                    linkListener: LinkOnClickListener) {
        adapter = ThreadAdapter(thread, picListener, linkListener)
    }

    fun getAdapter(): ThreadAdapter = this.adapter

    fun getBoardId(): String = this.boardId

    fun setCaptchaResponse(captchaResponse: String) {
        Log.d("M_ThreadPresenter", "presenter token = $captchaResponse")
        this.captchaResponse.postValue(captchaResponse)
    }

    fun setBoardId(): String = this.boardId

    fun clearStack() {
        this.modalStack.clear()
    }

    fun putContentInStack(modal: ModalContent) {
        this.modalStack.push(modal)
    }

    fun onBackPressed() {
        modalStack.pop()

        if(!modalStack.empty()) {

            when(val content = modalStack.peek()){
                is Attachment -> viewState.showPic(content)
                is Post -> viewState.showPost(content)
            }
        } else
            viewState.hideModal()
    }

    fun getSinglePost(postNum: Int) {
        getSinglePost(this.boardId, postNum)
    }

    fun getSinglePost(boardId: String, postNum: Int){
        disposables.add(
            repo.getPost(boardId, postNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        this.putContentInStack(it)
                        viewState.showPost(it)
                    },
                    { viewState.showToast("Пост не найден") }
                )

        )
    }

    fun setThreadMarks(){
        viewState.setThreadMarks(thread)
    }
}
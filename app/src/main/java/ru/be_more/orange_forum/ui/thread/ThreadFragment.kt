package ru.be_more.orange_forum.ui.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.android.synthetic.main.item_thread_response_form.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.POST_IN_THREAD_TAG
import ru.be_more.orange_forum.consts.POST_TAG
import ru.be_more.orange_forum.consts.RESPONSE_TAG
import ru.be_more.orange_forum.consts.THREAD_TAG
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.CustomOnScrollListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.PicOnClickListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.custom.CustomScrollListener
import ru.be_more.orange_forum.ui.post.PostFragment
import ru.be_more.orange_forum.ui.response.ResponseFragment



class ThreadFragment : MvpAppCompatFragment(),
    PicOnClickListener,
    LinkOnClickListener,
    ThreadView,
    CustomOnScrollListener,
    CloseModalListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var threadPresenter : ThreadPresenter

    private lateinit var boardId: String
    private var threadNum: Int = 0
    private lateinit var recyclerView : RecyclerView
    private var captchaResponse: MutableLiveData<String> = MutableLiveData()

    private var disposable: Disposable? = null


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        threadPresenter.init(boardId, threadNum)
        recyclerView = rv_post_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        disposable = App.getBus().subscribe({
            if(it.first is BackPressed && it.second == THREAD_TAG) {
                if (fl_thread_post.visibility != View.GONE)
                    threadPresenter.onBackPressed()
                else
                    App.getBus().onNext(Pair(AppToBeClosed, ""))
            }
            if (it.first is ThreadEntered && it.second == THREAD_TAG)
                threadPresenter.setThreadMarks()
        },
            {
                Log.e("M_ThreadFragment","bus error = \n $it")
            }
        )

        setUpDownButtonOnCLickListener()

        setOnScrollListener()

        fab_thread_respond.setOnClickListener { threadPresenter.showFooter() }

//        setOnBackgroundViewClickListener()

        //Swipe to refresh. maybe return later
        /*srl_thread.setColorSchemeColors(ContextCompat.getColor(App.applicationContext(), R.color.color_accent))
        srl_thread.setOnRefreshListener {
            srl_thread.isRefreshing = false
            threadPresenter.updateThreadData()
        }*/

        //TODO вернуть после API
/*        rv_post_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab_thread_respond.visibility == View.VISIBLE) {
                    fab_thread_respond.hide()
                } else if (dy < 0 && fab_thread_respond.visibility != View.VISIBLE ) {
                    fab_thread_respond.show()
                }
            }
        })*/

        /*fab_to_posting.setOnClickListener {
            setNewWebView()
        }*/
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun showResponseForm() { //TODO переделать на нормальную капчу, когда (если) макака сделает API

        val fragment = ResponseFragment()

        Log.d("M_ThreadFragment","123")

        fl_thread_post.visibility = View.VISIBLE
        fl_thread_post.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, fragment, RESPONSE_TAG)
            ?.commit()


    }
    override fun setNewWebView() {
/*

        wv_thread_posting.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView, url: String) {

                val css = "" +
//                        ".cntnt{display:none;}" +
                        "div.thread-nav-mob{display:none!important;}" +
//                        "#postform{display:block!important;}" +
                        ".rules{display:none!important;}"
                val js = "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                wv_thread_posting.evaluateJavascript(js,null)
                wv_thread_posting.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }
        }

        wv_thread_posting.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 " +
                    "Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]"

//        wv_thread_posting.visibility = View.VISIBLE
        wv_thread_posting.settings.javaScriptEnabled = true
        wv_thread_posting.settings.javaScriptCanOpenWindowsAutomatically = true
        wv_thread_posting.settings.builtInZoomControls = true
//        wv_thread_posting.settings.pluginState = WebSettings.PluginState.ON
        wv_thread_posting.settings.allowContentAccess = true
        wv_thread_posting.settings.domStorageEnabled = true
        wv_thread_posting.settings.loadWithOverviewMode = true
        wv_thread_posting.settings.useWideViewPort = true
        wv_thread_posting.settings.displayZoomControls = false
        wv_thread_posting.settings.setSupportZoom(true)
        wv_thread_posting.settings.defaultTextEncodingName = "utf-8"
        wv_thread_posting.loadUrl("https://2ch.hk/$boardId/res/$threadNum.html")
//        wv_thread_posting.loadDataWithBaseURL("https://2ch.hk/$boardId/res/$threadNum.html", "", "text/html; charset=UTF-8", null, null)
//        wv_thread_posting.addJavascriptInterface(ThreadFragment(), "Android")
*/

    }

    override fun loadThread(thread: BoardThread) {

        threadPresenter.initAdapter(thread, this, this)

        recyclerView.adapter = threadPresenter.getAdapter()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, HORIZONTAL)
        )
    }

    override fun setThreadMarks(isDownloaded: Boolean, isFavorite: Boolean){
        if (isDownloaded)
            App.getBus().onNext(Pair(DownloadedThreadEntered, ""))
        else
            App.getBus().onNext(Pair(UndownloadedThreadEntered, ""))

        if (isFavorite)
            App.getBus().onNext(Pair(FavoriteThreadEntered, ""))
        else
            App.getBus().onNext(Pair(UnfavoriteThreadEntered, ""))
    }

    override fun hideResponseFab() {
        fab_thread_respond.visibility = View.GONE
    }

    override fun setOnPostButtonClickListener() {

        captchaResponse.observe(this, Observer {
            threadPresenter.setCaptchaResponse(it)
        })


        btn_response_submit.setOnClickListener {

            CookieManager.getInstance().flush()
            Log.d("M_ThreadFragment", "google.com = "+CookieManager.getInstance().getCookie(".google.com"))
            Log.d("M_ThreadFragment", "www.google.com = "+CookieManager.getInstance().getCookie("www.google.com"))
            Log.d("M_ThreadFragment", "2ch.hk = "+CookieManager.getInstance().getCookie(".2ch.hk"))



            wv_post_captcha.loadUrl("javascript: Android.responsePushed(sendParams())")

//             threadPresenter.post()

        }
    }

    override fun onThumbnailListener(fullPicUrl: String?, duration: String?, fullPicUri: Uri?) {

        var attachment: Attachment? = null

        if (fullPicUri != null)
            attachment = Attachment("", duration, fullPicUri)
        else if (!fullPicUrl.isNullOrEmpty())
            attachment = Attachment(fullPicUrl, duration)

        if (attachment != null) {
            threadPresenter.putContentInStack(attachment)
            showPic(attachment)
            fl_thread_post.visibility = View.VISIBLE
        }

    }

    override fun onLinkClick(chanLink: Triple<String, Int, Int>?) {
        if (chanLink != null) {
            val post = threadPresenter.thread.posts.find { it.num == chanLink.third }

            if (post != null) {
                threadPresenter.putContentInStack(post)
                showPost(post)
            }
            else
                threadPresenter.getSinglePost(chanLink.first, chanLink.third)
        }
    }

    override fun onLinkClick(postNum: Int) {
        val post = threadPresenter.thread.posts.find { it.num == postNum }

        if (post != null) {
            threadPresenter.putContentInStack(post)
            showPost(post)
        }
        else
            threadPresenter.getSinglePost(postNum)
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun showPic(attachment: Attachment){
        val fragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, fragment, POST_IN_THREAD_TAG)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_thread_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_thread_post, fragment, POST_IN_THREAD_TAG)
            ?.commit()
    }

    override fun hideModal() {
        fl_thread_post.visibility = View.GONE

        App.getBus().onNext(Pair(VideoToBeClosed, POST_TAG))

        if (fragmentManager?.findFragmentByTag(POST_IN_THREAD_TAG) != null)
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragmentManager?.findFragmentByTag(POST_IN_THREAD_TAG)!!)

        threadPresenter.clearStack()
    }

    private fun setUpDownButtonOnCLickListener(){
        fab_thread_up.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }
        fab_thread_down.setOnClickListener {
            recyclerView.scrollToPosition(threadPresenter.getAdapter().itemCount - 1)
        }
    }

    private fun setOnScrollListener(){
        val scrollListener = CustomScrollListener(this)

        recyclerView.setOnScrollChangeListener(scrollListener)
    }

    override fun onScrolling(){
        Log.d("M_ThreadFragment", "scroll")
        fab_thread_up.visibility = View.VISIBLE
        fab_thread_down.visibility = View.VISIBLE
    }

    override fun onScrollStop(){
        Log.d("M_ThreadFragment", "stop scroll")
        fab_thread_up.visibility = View.GONE
        fab_thread_down.visibility = View.GONE
    }

    override fun onCloseModalListener(){
        hideModal()
    }

    override fun showToast(message: String) {
        Toast.makeText(App.applicationContext(), message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun responsePushed(token: String) {
        Log.d("M_ThreadFragment", "token = \"$token\"")
        threadPresenter.setCaptchaResponse(token)
        this.captchaResponse.postValue(token)
    }

    companion object {
        fun getThreadFragment ( boardId: String, threadId: Int): ThreadFragment {
            val thread = ThreadFragment()
            thread.boardId = boardId
            thread.threadNum = threadId

            return thread
        }
    }

}
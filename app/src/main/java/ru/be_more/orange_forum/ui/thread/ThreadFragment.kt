package ru.be_more.orange_forum.ui.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.android.synthetic.main.item_thread_response_form.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.AppToBeClosed
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.interfaces.CloseModalListener
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.interfaces.CustomOnScrollListener
import ru.be_more.orange_forum.model.Attachment
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.ui.custom.CustomScrollListener
import ru.be_more.orange_forum.ui.post.PostFragment
import ru.be_more.orange_forum.ui.post.PicOnClickListener

/*const val PAGE_HTML = "<html>\n" +
        "<head>\n" +
        "    <script type=\"text/javascript\">\n" +
        "    var sendParams = function() {\n" +
        "       var responseEl = document.getElementById(\"g-recaptcha-response\");\n" +
        "\t   var response = responseEl.value;\n" +
        "\t   return JSON.stringify(response);\n" +
        "    };\n" +
        "  </script>\n" +
        "    <script type=\"text/javascript\">\n" +
        "      var onloadCallback = function() {\n" +
        "        grecaptcha.execute()" +
        "      };\n" +
        "  </script>\n" +
        "</head>\n" +
        "<body>\n" +
        "<script src=\"//www.google.com/recaptcha/api.js?onload=onloadCallback\"\n" + //&render=explicit
        "        async defer>\n" +
        "</script>\n" +
        "<form id='demo-form' action=\"?\" method=\"POST\">\n" +
        "      <button class=\"g-recaptcha\" data-sitekey=\"6LdwXD4UAAAAAHxyTiwSMuge1-pf1ZiEL4qva_xu\" data-callback='onSubmit'>Submit</button>\n" +
        "      <br/>\n" +
        "    </form>" +
        "</body>\n" +
        "</html>\n" +
        "\n"*/


class ThreadFragment : MvpAppCompatFragment(),
    PicOnClickListener,
    LinkOnClickListener,
    ThreadView,
    CustomOnScrollListener,
    CloseModalListener {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var threadPresenter : ThreadPresenter

    private var timestamp: Long = 0
    private lateinit var boardId: String
    private var threadId: Int = 0
    private lateinit var recyclerView : RecyclerView
//    private lateinit var recyclerView : CustomRecyclerView
    private var captchaResponse: MutableLiveData<String> = MutableLiveData()

    private var bus = BusProvider.getInstance()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        threadPresenter.init(boardId, threadId)
        recyclerView = rv_post_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        bus.register(this)

//        setOnBackgroundViewClickListener()

        setUpDownButtonOnCLickListener()

        setOnScrollListener()

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

        fab_thread_respond.setOnClickListener { threadPresenter.showFooter() }
    }

    override fun onDestroy() {
        bus.unregister(this)
        super.onDestroy()
    }

    override fun setWebView() { //TODO переделать на нормальную капчу, когда (если) макака сделает API

/*        wv_post_captcha.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 " +
                    "Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]"

        wv_post_captcha.visibility = View.VISIBLE

        wv_post_captcha.settings.javaScriptEnabled = true
        wv_post_captcha.settings.javaScriptCanOpenWindowsAutomatically = true
        wv_post_captcha.settings.builtInZoomControls = true
        wv_post_captcha.settings.pluginState = WebSettings.PluginState.ON
        wv_post_captcha.settings.allowContentAccess = true
        wv_post_captcha.settings.domStorageEnabled = true

        wv_post_captcha.settings.loadWithOverviewMode = true
        wv_post_captcha.settings.useWideViewPort = true
        wv_post_captcha.settings.displayZoomControls = false
        wv_post_captcha.settings.setSupportZoom(true)
        wv_post_captcha.settings.defaultTextEncodingName = "utf-8"

        wv_post_captcha.loadDataWithBaseURL("https://2ch.hk", PAGE_HTML, "text/html; charset=UTF-8", null, null)
        wv_post_captcha.addJavascriptInterface(ThreadFragment(), "Android")*/

    }

    override fun loadThread(thread: BoardThread) {

        threadPresenter.initAdapter(thread, this, this)

        recyclerView.adapter = threadPresenter.getAdapter()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, HORIZONTAL)
        )
    }

    override fun hideResponseFab() {
        fab_thread_respond.visibility = View.GONE
    }

    override fun setOnPostButtonClickListener() {

        captchaResponse.observe(this, Observer {
            Log.d("M_ThreadFragment", "trigger")
            threadPresenter.setCaptchaResponse(it)
        })


        btn_response_submit.setOnClickListener {
//            wv_post_captcha.loadUrl("javascript: Android.responsePushed(sendParams())")

            Toast.makeText(App.applicationContext(),
                "Постинг отключен пока макака не сделает API", Toast.LENGTH_LONG).show()
        }

        //        btn_response_submit.setOnClickListener { threadPresenter.post() }
    }

    override fun onThumbnailListener(fullPicUrl: String, duration: String?) {

        fl_post.visibility = View.VISIBLE

        val attachment = Attachment(fullPicUrl, duration)

        threadPresenter.putContentInStack(attachment)

        showPic(attachment)
    }

    override fun onLinkClick(chanLink: Triple<String, String, String>?) {

        if (chanLink != null) {
            val post = threadPresenter.thread.posts.find { it.num == chanLink.third.toInt() }

            if (post != null) {
                threadPresenter.putContentInStack(post)
                showPost(post)
            }
            else
                ""//TODO если не найден, то запрос по вебу из другого треда
        }
    }

    override fun onLinkClick(postNum: Int) {

        val post = threadPresenter.thread.posts.find { it.num == postNum }

        if (post != null) {
            threadPresenter.putContentInStack(post)
            showPost(post)
        }
        else
            true //TODO если не найден, то запрос по вебу из другого треда
    }

    override fun onLinkClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
    }

    override fun showPic(attachment: Attachment){
        val fragment = PostFragment.getPostFragment(
            attachment,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun showPost(post: Post){

        fl_post.visibility = View.VISIBLE

        val fragment = PostFragment.getPostFragment(
            post,this,this, this)

        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fl_post, fragment, fragment.javaClass.simpleName)
            ?.commit()
    }

    override fun hideModal() {
        fl_post.visibility = View.GONE
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


    @Subscribe
    public fun onBackPressed(event: BackPressed) {

        Log.d("M_ThreadFragment", "back")
        if (fl_post.visibility != View.GONE)
            threadPresenter.onBackPressed()
        else
            bus.post(AppToBeClosed)

    }

    @JavascriptInterface
    fun responsePushed(token: String) {
//        Log.d("M_ThreadFragment", "token = \"$token\"")
//        threadPresenter.setCaptchaResponse(token)
//        this.captchaResponse.postValue(token)
    }

    companion object {
        fun getThreadFragment ( boardId: String, threadId: Int): ThreadFragment {
            val thread = ThreadFragment()
            thread.boardId = boardId
            thread.threadId = threadId

            return thread
        }
    }

}
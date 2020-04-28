package ru.be_more.orange_forum.ui.thread

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.widget.MediaController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_thread.*
import kotlinx.android.synthetic.main.item_thread_response_form.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.interfaces.LinkOnClickListener
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.ui.post.PostOnClickListener

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
    PostOnClickListener,
    LinkOnClickListener,
    ThreadView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var threadPresenter : ThreadPresenter

    private var timestamp: Long = 0
    private lateinit var boardId: String
    private var threadId: Int = 0
    private lateinit var recyclerView : RecyclerView
    private var captchaResponse: MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.fragment_thread, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        threadPresenter.init(boardId, threadId)
        recyclerView = rv_post_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)


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

    override fun setOnPostClickListener() {

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

        v_post_pic_full_background.visibility = View.VISIBLE
        pb_post_pic_loading.visibility = View.VISIBLE

        if(duration == "") {
            val fullPicGlideUrl = GlideUrl(
                fullPicUrl,
                LazyHeaders.Builder()
                    .addHeader(
                        "Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                                "_ga=GA1.2.57010468.1498700728; " +
                                "ageallow=1; " +
                                "_gid=GA1.2.1910512907.1585793763; " +
                                "_gat=1"
                    )
                    .build()
            )
            iv_post_pic_full.resetZoom()
            iv_post_pic_full.visibility = View.VISIBLE
            Glide.with(this)
                .load(fullPicGlideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("M_BoardFragment", "$e")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pb_post_pic_loading.visibility = View.GONE
                        return false
                    }
                })
                .into(iv_post_pic_full)

            iv_post_pic_full.setOnClickListener {
                v_post_pic_full_background.visibility = View.GONE
                iv_post_pic_full.visibility = View.GONE
            }
        }
        else{
            pb_post_pic_loading.visibility = View.VISIBLE

            //TODO потом поменять на нормальные куки
            val headers = mapOf("cookie" to
                    "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                    "_ga=GA1.2.57010468.1498700728; " +
                    "ageallow=1; " +
                    "_gid=GA1.2.1910512907.1585793763; " +
                    "_gat=1")

            vv_post_video.setOnPreparedListener { pb_post_pic_loading.visibility = View.GONE }

            vv_post_video.setVideoURI(Uri.parse(fullPicUrl), headers)
            vv_post_video.visibility = View.VISIBLE
            vv_post_video.setMediaController(MediaController(this.context))
            vv_post_video.requestFocus(0)
            vv_post_video.start()

            vv_post_video.setOnClickListener {
                if(System.currentTimeMillis() - timestamp < 3000) {
                    pb_post_pic_loading.visibility = View.GONE
                    vv_post_video.visibility = View.GONE
                    v_post_pic_full_background.visibility = View.GONE
                }
                else
                    timestamp = System.currentTimeMillis()
            }
        }

        v_post_pic_full_background.setOnClickListener {
            v_post_pic_full_background.visibility = View.GONE
            iv_post_pic_full.visibility = View.GONE
            Glide.with(this).clear(iv_post_pic_full)
            pb_post_pic_loading.visibility = View.GONE
            vv_post_video.visibility = View.GONE
        }
    }


    override fun onClick(chanLink: Triple<String, String, String>?) {
        Log.d("M_ThreadPresenter", "inner link = $chanLink")
    }

    override fun onClick(externalLink: String?) {
        Log.d("M_ThreadPresenter", "outer link = $externalLink")
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
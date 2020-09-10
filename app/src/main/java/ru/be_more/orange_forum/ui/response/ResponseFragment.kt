package ru.be_more.orange_forum.ui.response

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.item_thread_response_form.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.PAGE_HTML

class ResponseFragment(val boardId: String, val threadNum: Int): MvpAppCompatFragment(), ResponseView{

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var responsePresenter : ResponsePresenter

    val captchaResponse : MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.item_thread_response_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setWebView()

        btn_response_submit.setOnClickListener {
            posting()
        }

        captchaResponse.observeForever {token ->
            responsePresenter.postResponse(
                boardId,
                threadNum,
                et_response_comment.text.toString(),
                token
            )
        }

    }

    private fun setWebView(){

        Log.d("M_ThreadFragment", ""+ CookieManager.getInstance().getCookie("google.com"))

        wv_post_captcha.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 " +
                    "Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]"

//        wv_post_captcha.visibility = View.VISIBLE

        Log.d("M_ThreadFragment",""+ CookieManager.getInstance().acceptCookie())
        Log.d("M_ThreadFragment",""+ CookieManager.getInstance().hasCookies())

        wv_post_captcha.settings.javaScriptEnabled = true
        wv_post_captcha.settings.javaScriptCanOpenWindowsAutomatically = true
        wv_post_captcha.settings.builtInZoomControls = true
        wv_post_captcha.settings.pluginState = WebSettings.PluginState.ON
        wv_post_captcha.settings.allowContentAccess = true
        wv_post_captcha.settings.domStorageEnabled = true
        wv_post_captcha.settings.loadWithOverviewMode = true
        wv_post_captcha.settings.useWideViewPort = false
        wv_post_captcha.settings.displayZoomControls = false
        wv_post_captcha.settings.setSupportZoom(false)
        wv_post_captcha.settings.defaultTextEncodingName = "utf-8"
        wv_post_captcha.setInitialScale(200)

        wv_post_captcha.loadDataWithBaseURL(
            "https://2ch.hk",
            PAGE_HTML,
            "text/html; charset=UTF-8",
            null,
            null
        )
        wv_post_captcha.addJavascriptInterface(this, "Android")
    }

    private fun posting(){
        CookieManager.getInstance().flush()
//        Log.d("M_ThreadFragment", "google.com = "+CookieManager.getInstance().getCookie(".google.com"))
//        Log.d("M_ThreadFragment", "www.google.com = "+CookieManager.getInstance().getCookie("www.google.com"))
//        Log.d("M_ThreadFragment", "2ch.hk = "+CookieManager.getInstance().getCookie(".2ch.hk"))



        wv_post_captcha.loadUrl("javascript: Android.responsePushed(sendParams())")

    }


    @JavascriptInterface
    fun responsePushed(token: String) {
        Log.d("M_ThreadFragment", "token = $token")
        captchaResponse.postValue(token.substring(1, token.length-1))

    }

}
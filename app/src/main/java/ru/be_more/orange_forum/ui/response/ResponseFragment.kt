package ru.be_more.orange_forum.ui.response

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.item_thread_response_form.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.PAGE_HTML

class ResponseFragment(val boardId: String, val threadNum: Int): Fragment(), ResponseView{

    private val responsePresenter: ResponsePresenter by inject(parameters = { parametersOf(this) })

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
        wv_post_captcha.loadUrl("javascript: Android.responsePushed(sendParams())")
    }


    @JavascriptInterface
    fun responsePushed(token: String) {
        captchaResponse.postValue(token.substring(1, token.length-1))
    }

}
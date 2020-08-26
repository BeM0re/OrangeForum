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

//TODO вынести в отдельный файл куда-нибудь
const val PAGE_HTML = "<html>\n" +
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
        "         grecaptcha.render('html_element', {\n" +
        "          'sitekey' : '6LeQYz4UAAAAAL8JCk35wHSv6cuEV5PyLhI6IxsM' \n" +
        "        });" +
        "      };\n" +
        "  </script>\n" +
        "</head>\n" +
        "<body style=\"\nbackground: #eeeeee;\">\n" +
        "<form action=\"?\" method=\"POST\">\n\n" +
        "      <div id=\"html_element\"></div>" +
        "      <br/>\n" +
        "    </form>" +
        "<script src=\"//www.google.com/recaptcha/api.js?onload=onloadCallback\"\n" + "&render=explicit" +
        "        async defer>\n" +
        "</script>\n" +
        "</body>\n" +
        "</html>\n" +
        "\n"

class ResponseFragment(val boardId: String, val threadNum: Int): MvpAppCompatFragment(), ResponseView{

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var responsePresenter : ResponsePresenter

    private val captchaResponse : MutableLiveData<String> = MutableLiveData()

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

        Log.d("M_ThreadFragment",""+ CookieManager.getInstance().hasCookies())
        Log.d("M_ThreadFragment", "google.com = "+CookieManager.getInstance().getCookie(".google.com"))

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

        CookieManager.getInstance().setAcceptThirdPartyCookies(wv_post_captcha, true)

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
        Log.d("M_ThreadFragment", "token = $token")
        captchaResponse.postValue(token.substring(1, token.length-1))

    }

}
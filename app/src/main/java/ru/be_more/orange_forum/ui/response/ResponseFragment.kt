package ru.be_more.orange_forum.ui.response

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.item_thread_response_form.*
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.PAGE_HTML
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.ui.PresentationContract

class ResponseFragment: Fragment(){

    private val viewModel: PresentationContract.ResponseViewModel by inject()
    private lateinit var navController: NavController
    private var captchaResponse : MutableLiveData<String>? = MutableLiveData()
    private lateinit var boardId: String
    private var threadNum: Int = 0
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? =
        inflater.inflate(R.layout.item_thread_response_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        setWebView()

        btn_response_submit.setOnClickListener {
            posting()
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)

        boardId = requireArguments().getString("boardId")?:""
        threadNum = requireArguments().getInt("threadNum")
    }

    private fun subscribe(){
        with(viewModel){
            observe(result, ::handleResult)
        }

        captchaResponse?.observe(viewLifecycleOwner, Observer{token ->
            viewModel.postResponse(
                boardId,
                threadNum,
                et_response_comment.text.toString(),
                token
            )
        })

        disposable = App.getBus().subscribe(
            {
                if(it is BackPressed ) {
                    navController.navigateUp()
                }
            },
            {
                Log.e("M_ThreadFragment","bus error = \n $it")
            }
        )
    }

    //TODO save state
    override fun onDestroyView() {
        viewModel.onDestroy()
        captchaResponse?.removeObservers(this)
        captchaResponse = null
        disposable?.dispose()
        disposable = null
        super.onDestroyView()
    }

    private fun setWebView(){

        wv_post_captcha.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 " +
                    "Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]"

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

        setHideKeyboardListener()
    }

    private fun setHideKeyboardListener(){
        et_response_comment.setOnFocusChangeListener{ view, isFocused ->
            if(!isFocused)
                (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        }

        et_response_option.setOnFocusChangeListener{ view, isFocused ->
            if(!isFocused)
                (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun posting(){
        CookieManager.getInstance().flush()
        wv_post_captcha.loadUrl("javascript: Android.responsePushed(sendParams())")
    }

    private fun handleResult(result: String) {
        if (result.isEmpty()){
            App.showToast("Отправлено")
            App.getBus().onNext(BackPressed)
        }
        else
            App.showToast(result)
    }

    @JavascriptInterface
    fun responsePushed(token: String) {
        captchaResponse?.postValue(token.substring(1, token.length-1))
    }


}
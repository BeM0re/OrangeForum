package ru.be_more.orange_forum.presentation.screens.response

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import org.koin.android.ext.android.inject
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.consts.PAGE_HTML
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.databinding.ItemThreadResponseFormBinding
import ru.be_more.orange_forum.extentions.LifecycleOwnerExtensions.observe
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment

class ResponseFragment: BaseFragment<ItemThreadResponseFormBinding>(){

    override val binding: ItemThreadResponseFormBinding by viewBinding()
    private val viewModel: PresentationContract.ResponseViewModel by inject()
    private lateinit var navController: NavController
    private var captchaResponse : MutableLiveData<String>? = MutableLiveData()
    private lateinit var boardId: String
    private var threadNum: Int = 0
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.item_thread_response_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        subscribe()
        setWebView()

        binding.btnResponseSubmit.setOnClickListener {
            posting()
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)

        boardId = requireArguments().getString("boardId")?:""
        threadNum = requireArguments().getInt("threadNum")
    }

    private fun subscribe(){
        observe(viewModel.result, ::handleResult)

        captchaResponse?.observe(viewLifecycleOwner, Observer{token ->
            viewModel.postResponse(
                boardId,
                threadNum,
                binding.etResponseComment.text.toString(),
                token
            )
        })
    }

    override fun onBackPressed() : Boolean{
        navController.navigateUp()
        return false
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(){

        binding.wvPostCaptcha.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 " +
                    "Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]"

        binding.wvPostCaptcha.settings.javaScriptEnabled = true
        binding.wvPostCaptcha.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.wvPostCaptcha.settings.builtInZoomControls = true
        binding.wvPostCaptcha.settings.pluginState = WebSettings.PluginState.ON
        binding.wvPostCaptcha.settings.allowContentAccess = true
        binding.wvPostCaptcha.settings.domStorageEnabled = true
        binding.wvPostCaptcha.settings.loadWithOverviewMode = true
        binding.wvPostCaptcha.settings.useWideViewPort = false
        binding.wvPostCaptcha.settings.displayZoomControls = false
        binding.wvPostCaptcha.settings.setSupportZoom(false)
        binding.wvPostCaptcha.settings.defaultTextEncodingName = "utf-8"

        binding.wvPostCaptcha.setInitialScale(200)

        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.wvPostCaptcha, true)

        binding.wvPostCaptcha.loadDataWithBaseURL(
            "https://2ch.hk",
            PAGE_HTML,
            "text/html; charset=UTF-8",
            null,
            null
        )
        binding.wvPostCaptcha.addJavascriptInterface(this, "Android")

        setHideKeyboardListener()
    }

    private fun setHideKeyboardListener(){
        binding.etResponseComment.setOnFocusChangeListener{ view, isFocused ->
            if(!isFocused)
                (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        }

        binding.etResponseOption.setOnFocusChangeListener{ view, isFocused ->
            if(!isFocused)
                (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun posting(){
        CookieManager.getInstance().flush()
        binding.wvPostCaptcha.loadUrl("javascript: Android.responsePushed(sendParams())")
    }

    private fun handleResult(result: String) {
        if (result.isEmpty()){
            Toast.makeText(requireContext(), "Отправлено", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
        }
        else
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun responsePushed(token: String) {
        captchaResponse?.postValue(token.substring(1, token.length-1))
    }


}
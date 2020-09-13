package ru.be_more.orange_forum.data.remote.utils

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.*


class SSLTrustManager @Inject constructor() {

    val trustAllCerts: Array<TrustManager> = arrayOf(
        @SuppressLint("TrustAllX509TrustManager")
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> { return arrayOf() }
        }
    )

    val X509TrustManager
        get() = trustAllCerts[0] as X509TrustManager

    val socketFactory: SSLSocketFactory
        get() = SSLContext.getInstance("SSL")
            .apply { init(null, trustAllCerts, SecureRandom()) }
            .socketFactory

    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { _, _ -> true }


}
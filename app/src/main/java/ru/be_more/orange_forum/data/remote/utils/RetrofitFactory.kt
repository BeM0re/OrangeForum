package ru.be_more.orange_forum.data.remote.utils

import br.com.jeancsanchez.restinterceptor.RestErrorInterceptor
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitFactory{

    private val sslTrustManager =
        SSLTrustManager()
    var gson = GsonBuilder()
        .setLenient()
        .create()

    //Not logging the authkey if not debug
    private val client =
        OkHttpClient().newBuilder()
//            .addInterceptor(HttpLoggingInterceptor { Log.v("M_RetrofitFactory", it) }
//                .apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor(RestErrorInterceptor())
            .sslSocketFactory(sslTrustManager.socketFactory, sslTrustManager.X509TrustManager)
            .hostnameVerifier(sslTrustManager.hostnameVerifier)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()

    fun retrofit(baseUrl : String) : Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()


}


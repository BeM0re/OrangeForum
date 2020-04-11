package ru.be_more.orange_forum.services

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitFactory{



    private val sslTrustManager = SSLTrustManager()

    //Not logging the authkey if not debug
    private val client =
        OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor { Log.d(OkHttpClient::class.java.simpleName, it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY })
            .sslSocketFactory(sslTrustManager.socketFactory, sslTrustManager.X509TrustManager)
            .hostnameVerifier(sslTrustManager.hostnameVerifier)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()

    /*fun retrofit(baseUrl : String) : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()*/

    fun retrofit(baseUrl : String) : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}
package ru.be_more.orange_forum.services

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.be_more.orange_forum.BuildConfig
import java.util.*
import java.util.concurrent.TimeUnit


object RetrofitFactory{


    private val authInterceptor = Interceptor {chain->
        val newUrl = chain.request().url()
            .newBuilder()
            .addQueryParameter("task", "get_boards")
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    private val loggingInterceptor =  HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val sslTrustManager = SSLTrustManager()

    //Not logging the authkey if not debug
    private val client =
        if(BuildConfig.DEBUG){
            Log.d("M_RetrofitFactory", "release")
            OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor { Log.d(OkHttpClient::class.java.simpleName, it) }.apply { level = HttpLoggingInterceptor.Level.BODY })
//                .addInterceptor(authInterceptor)
//                .addInterceptor(loggingInterceptor)
                .sslSocketFactory(sslTrustManager.socketFactory, sslTrustManager.X509TrustManager)
                .hostnameVerifier(sslTrustManager.hostnameVerifier)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .build()
        }else{
            Log.d("M_RetrofitFactory", "debug")
            OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .sslSocketFactory(sslTrustManager.socketFactory, sslTrustManager.X509TrustManager)
                .hostnameVerifier(sslTrustManager.hostnameVerifier)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .build()
        }

    fun retrofit(baseUrl : String) : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}
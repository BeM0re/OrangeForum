package ru.be_more.orange_forum.di.modules

import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.data.remote.utils.ApiFactory
import ru.be_more.orange_forum.data.remote.utils.RetrofitFactory
import ru.be_more.orange_forum.data.remote.utils.SSLTrustManager

@Module
class NetworkModule {
    @Provides
    fun provideSllTrustManager():
            SSLTrustManager = SSLTrustManager()

    @Provides
    fun provideRetrofitFactory( sslTrustManager : SSLTrustManager):
            RetrofitFactory = RetrofitFactory(sslTrustManager)

    @Provides
    fun provideApiFactory( retrofitFactory: RetrofitFactory):
            ApiFactory = ApiFactory(retrofitFactory)

}
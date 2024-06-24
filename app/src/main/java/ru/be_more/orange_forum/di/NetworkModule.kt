package ru.be_more.orange_forum.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.repositories.ApiRepositoryImpl
import ru.be_more.orange_forum.data.remote.service.RetrofitFactory
import ru.be_more.orange_forum.data.remote.service.SSLTrustManager
import ru.be_more.orange_forum.domain.contracts.RemoteContract

@Module
class NetworkModule {

    @Provides
    fun provideSslTrustManager(): SSLTrustManager {
        return SSLTrustManager()
    }

    @Provides
    fun provideApiService(
        sslTrustManager: SSLTrustManager,
        context: Context
    ): DvachApi {
        return RetrofitFactory(sslTrustManager, context)
            .retrofit(DVACH_ROOT_URL)
            .create(DvachApi::class.java)
    }
}

@Module
interface NetworkBindModule {
    @Binds
    fun bindApiRepo(apiRepositoryImpl: ApiRepositoryImpl): RemoteContract.ApiRepository
}
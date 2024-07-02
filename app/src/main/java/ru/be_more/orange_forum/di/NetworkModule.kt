package ru.be_more.orange_forum.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.be_more.model.model.Imageboard
import ru.be_more.model.model.ImageboardType
import ru.be_more.network.api.DvachApi
import ru.be_more.network.api.FourchanApi
import ru.be_more.orange_forum.data.remote.repositories.DvachApiRepositoryImpl
import ru.be_more.network.service.RetrofitFactory
import ru.be_more.network.service.SSLTrustManager
import ru.be_more.orange_forum.data.remote.repositories.FourchanApiRepositoryImpl
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun provideSslTrustManager(): SSLTrustManager {
        return SSLTrustManager()
    }

    @Provides
    fun provideDvachApiService(
        sslTrustManager: SSLTrustManager,
        context: Context,
        @Dvach imageboard: Imageboard,
    ): DvachApi {
        return RetrofitFactory(sslTrustManager, context)
            .retrofit(imageboard.baseUrl)
            .create(DvachApi::class.java)
    }

    @Provides
    fun provideFourchanApiService(
        sslTrustManager: SSLTrustManager,
        context: Context,
        @Fourchan imageboard: Imageboard,
    ): FourchanApi {
        return RetrofitFactory(sslTrustManager, context)
            .retrofit(imageboard.baseUrl)
            .create(FourchanApi::class.java)
    }

    @Dvach
    @Provides
    fun provideDvachUrls() : Imageboard {
        return Imageboard(
            baseUrl = "https://2ch.hk",
            attachmentUrl = "https://2ch.hk",
            staticDataUrl = "https://2ch.hk",
            type = ImageboardType.Dvach,
        )
    }

    @Fourchan
    @Provides
    fun provideFourchanUrls() : Imageboard {
        return Imageboard(
            baseUrl = "https://a.4cdn.org",
            attachmentUrl = "https://i.4cdn.org",
            staticDataUrl = "https://s.4cdn.org",
            type = ImageboardType.Fourchan,
        )
    }
}

@Module
interface NetworkBindModule {
    @Binds
    @IntoMap
    @Singleton
    @ApiKey(ImageboardType.Dvach)
    fun bindDvachApiRepo(dvachApiRepositoryImpl: DvachApiRepositoryImpl): RemoteContract.ApiRepository

    @Binds
    @IntoMap
    @Singleton
    @ApiKey(ImageboardType.Fourchan)
    fun bindFourchanApiRepo(fourchanApiRepositoryImpl: FourchanApiRepositoryImpl): RemoteContract.ApiRepository
}
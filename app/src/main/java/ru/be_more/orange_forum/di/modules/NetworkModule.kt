package ru.be_more.orange_forum.di.modules

//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
import org.koin.dsl.module
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.service.ApiFactory
import ru.be_more.orange_forum.data.remote.service.RetrofitFactory
import ru.be_more.orange_forum.data.remote.service.SSLTrustManager

/*
@Module
//@InstallIn(ApplicationComponent::class)
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

}*/

@JvmField
val networkModule = module {
    single { SSLTrustManager() }
    single { RetrofitFactory(get()) }
    single {
        RetrofitFactory(get())
            .retrofit(DVACH_ROOT_URL)
            .create(DvachApi::class.java)
    }
}
package ru.be_more.orange_forum.di.modules

import org.koin.dsl.module
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.service.RetrofitFactory
import ru.be_more.orange_forum.data.remote.service.SSLTrustManager

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
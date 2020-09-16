package ru.be_more.orange_forum.data.remote.service

import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.data.remote.api.DvachApi
//import javax.inject.Inject
//import javax.inject.Singleton

//@Singleton
class ApiFactory /*@Inject constructor*/(
    retrofitFactory: RetrofitFactory
) {

    val dvachApi : DvachApi = retrofitFactory.retrofit(DVACH_ROOT_URL)
        .create(DvachApi::class.java)
}
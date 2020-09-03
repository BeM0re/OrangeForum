package ru.be_more.orange_forum.data.remote.utils

import ru.be_more.orange_forum.data.remote.api.DvachApi

const val DVACH_ROOT_URL = "https://2ch.hk/"

object ApiFactory{

    val dvachApi : DvachApi = RetrofitFactory.retrofit(DVACH_ROOT_URL)
        .create(DvachApi::class.java)



}